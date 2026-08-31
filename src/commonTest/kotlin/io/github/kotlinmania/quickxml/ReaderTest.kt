// port-lint: tests quick-xml/src/reader/mod.rs
package io.github.kotlinmania.quickxml

import io.github.kotlinmania.quickxml.events.Event
import io.github.kotlinmania.quickxml.reader.Reader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ReaderTest {
    @Test
    fun testSimpleRead() {
        val xml = "<root><child key=\"val\">Hello World</child></root>"
        val reader = Reader.fromStr(xml)

        val e1 = reader.readEvent()
        assertTrue(e1 is Event.Start)
        assertEquals("root", e1.event.name().asString())

        val e2 = reader.readEvent()
        assertTrue(e2 is Event.Start)
        assertEquals("child", e2.event.name().asString())
        val attrs = e2.event.attributes().toList()
        assertEquals(1, attrs.size)
        assertEquals("key", attrs[0].key.asString())
        assertEquals("val", attrs[0].value.decodeToString())

        val e3 = reader.readEvent()
        assertTrue(e3 is Event.Text)
        assertEquals("Hello World", e3.event.asString())

        val e4 = reader.readEvent()
        assertTrue(e4 is Event.End)
        assertEquals("child", e4.event.name().asString())

        val e5 = reader.readEvent()
        assertTrue(e5 is Event.End)
        assertEquals("root", e5.event.name().asString())

        val e6 = reader.readEvent()
        assertEquals(Event.Eof, e6)
    }

    @Test
    fun testSelfClosingTag() {
        val xml = "<root><item/></root>"
        val reader = Reader.fromStr(xml)

        assertTrue(reader.readEvent() is Event.Start)
        val item = reader.readEvent()
        assertTrue(item is Event.Empty)
        assertEquals("item", item.event.name().asString())
        assertTrue(reader.readEvent() is Event.End)
        assertEquals(Event.Eof, reader.readEvent())
    }

    @Test
    fun testExpandEmptyElements() {
        val xml = "<item/>"
        val reader = Reader.fromStr(xml).expandEmptyElements(true)

        val start = reader.readEvent()
        assertTrue(start is Event.Start)
        assertEquals("item", start.event.name().asString())

        val end = reader.readEvent()
        assertTrue(end is Event.End)
        assertEquals("item", end.event.name().asString())

        assertEquals(Event.Eof, reader.readEvent())
    }

    @Test
    fun testCommentAndCData() {
        val xml = "<root><!-- my comment --><![CDATA[some <raw> content]]></root>"
        val reader = Reader.fromStr(xml)

        assertTrue(reader.readEvent() is Event.Start)

        val comment = reader.readEvent()
        assertTrue(comment is Event.Comment)
        assertEquals(" my comment ", comment.event.asString())

        val cdata = reader.readEvent()
        assertTrue(cdata is Event.CData)
        assertEquals("some <raw> content", cdata.event.asString())

        assertTrue(reader.readEvent() is Event.End)
        assertEquals(Event.Eof, reader.readEvent())
    }

    @Test
    fun testDeclarationAndPI() {
        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?target instruction?><root/>"
        val reader = Reader.fromStr(xml)

        val decl = reader.readEvent()
        assertTrue(decl is Event.Decl)
        assertEquals("1.0", decl.event.version())
        assertEquals("UTF-8", decl.event.encoding())

        val pi = reader.readEvent()
        assertTrue(pi is Event.PI)
        assertEquals("target", pi.event.target().asString())

        assertTrue(reader.readEvent() is Event.Empty)
        assertEquals(Event.Eof, reader.readEvent())
    }

    @Test
    fun testMismatchedEndTag() {
        val xml = "<a></b>"
        val reader = Reader.fromStr(xml)
        reader.readEvent()
        assertFailsWith<IllFormedError.MismatchedEndTag> {
            reader.readEvent()
        }
    }
}
