// port-lint: tests events/attributes.rs
package io.github.kotlinmania.quickxml

import io.github.kotlinmania.quickxml.events.AttrError
import io.github.kotlinmania.quickxml.events.Attributes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AttributesTest {
    @Test
    fun testParseAttributes() {
        val str = " a=\"1\" b='2' c=\"hello world\""
        val attrs = Attributes(str).toList()
        assertEquals(3, attrs.size)
        assertEquals("a", attrs[0].key.asString())
        assertEquals("1", attrs[0].value.decodeToString())
        assertEquals("b", attrs[1].key.asString())
        assertEquals("2", attrs[1].value.decodeToString())
        assertEquals("c", attrs[2].key.asString())
        assertEquals("hello world", attrs[2].value.decodeToString())
    }

    @Test
    fun testHtmlAttributes() {
        val str = " disabled autofocus value=123"
        val attrs = Attributes.html(str).toList()
        assertEquals(3, attrs.size)
        assertEquals("disabled", attrs[0].key.asString())
        assertEquals("", attrs[0].value.decodeToString())
        assertEquals("autofocus", attrs[1].key.asString())
        assertEquals("", attrs[1].value.decodeToString())
        assertEquals("value", attrs[2].key.asString())
        assertEquals("123", attrs[2].value.decodeToString())
    }

    @Test
    fun testDuplicateAttributes() {
        val str = " a=\"1\" a=\"2\""
        assertFailsWith<AttrError.Duplicated> {
            Attributes(str).toList()
        }
    }
}
