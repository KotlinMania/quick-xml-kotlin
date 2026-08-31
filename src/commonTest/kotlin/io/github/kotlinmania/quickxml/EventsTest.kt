// port-lint: tests quick-xml/src/events/mod.rs
package io.github.kotlinmania.quickxml

import io.github.kotlinmania.quickxml.events.BytesDecl
import io.github.kotlinmania.quickxml.events.BytesStart
import io.github.kotlinmania.quickxml.events.BytesText
import kotlin.test.Test
import kotlin.test.assertEquals

class EventsTest {
    @Test
    fun testBytesStart() {
        val elem = BytesStart.from("root")
        assertEquals("root", elem.name().asString())
        elem.pushAttribute("k", "v")
        val attrs = elem.attributes().toList()
        assertEquals(1, attrs.size)
        assertEquals("k", attrs[0].key.asString())
        assertEquals("v", attrs[0].value.decodeToString())
    }

    @Test
    fun testBytesDecl() {
        val decl = BytesDecl("xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"")
        assertEquals("1.0", decl.version())
        assertEquals("UTF-8", decl.encoding())
        assertEquals("yes", decl.standalone())
    }

    @Test
    fun testBytesTextUnescape() {
        val text = BytesText("foo &amp; bar &lt;tag&gt;")
        assertEquals("foo & bar <tag>", text.unescape())
    }
}
