// port-lint: tests writer.rs
package io.github.kotlinmania.quickxml

import io.github.kotlinmania.quickxml.events.BytesDecl
import io.github.kotlinmania.quickxml.events.BytesEnd
import io.github.kotlinmania.quickxml.events.BytesStart
import io.github.kotlinmania.quickxml.events.BytesText
import io.github.kotlinmania.quickxml.events.Event
import io.github.kotlinmania.quickxml.writer.Writer
import kotlin.test.Test
import kotlin.test.assertEquals

class WriterTest {
    @Test
    fun testWriterEvents() {
        val writer = Writer()
        writer.writeEvent(Event.Decl(BytesDecl.new("1.0", "UTF-8")))
        writer.writeEvent(Event.Start(BytesStart("root")))
        writer.writeEvent(Event.Start(BytesStart("child")))
        writer.writeEvent(Event.Text(BytesText("content")))
        writer.writeEvent(Event.End(BytesEnd("child")))
        writer.writeEvent(Event.Empty(BytesStart("empty")))
        writer.writeEvent(Event.End(BytesEnd("root")))

        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child>content</child><empty/></root>"
        assertEquals(expected, writer.intoInner().toString())
    }

    @Test
    fun testWriterWithIndent() {
        val sb = StringBuilder()
        val writer = Writer.newWithIndent(sb, ' ', 2)
        writer.writeEvent(Event.Start(BytesStart("root")))
        writer.writeEvent(Event.Start(BytesStart("child")))
        writer.writeEvent(Event.Text(BytesText("hello")))
        writer.writeEvent(Event.End(BytesEnd("child")))
        writer.writeEvent(Event.End(BytesEnd("root")))

        val expected = "<root>\n  <child>hello</child>\n</root>"
        assertEquals(expected, writer.intoInner().toString())
    }

    @Test
    fun testElementWriter() {
        val writer = Writer()
        writer
            .createElement("root")
            .withAttribute("key", "val")
            .writeTextContent("inside")

        assertEquals("<root key=\"val\">inside</root>", writer.intoInner().toString())
    }

    @Test
    fun testElementWriterInner() {
        val writer = Writer()
        writer
            .createElement("parent")
            .writeInnerContent { w ->
                w.createElement("item1").writeEmpty()
                w.createElement("item2").writeTextContent("val2")
            }

        assertEquals("<parent><item1/><item2>val2</item2></parent>", writer.intoInner().toString())
    }
}
