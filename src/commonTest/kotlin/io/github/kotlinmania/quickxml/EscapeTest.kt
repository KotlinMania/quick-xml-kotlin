// port-lint: tests quick-xml/src/escape.rs
package io.github.kotlinmania.quickxml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EscapeTest {
    @Test
    fun testEscape() {
        assertEquals("&lt;a&gt;&amp;&apos;&quot;", escape("<a>&'\""))
        assertEquals("&lt;hello&gt;", escape("<hello>"))
    }

    @Test
    fun testPartialEscape() {
        assertEquals("&lt;a&gt;&amp;'\"", partialEscape("<a>&'\""))
    }

    @Test
    fun testMinimalEscape() {
        assertEquals("&lt;a>&amp;'\"", minimalEscape("<a>&'\""))
    }

    @Test
    fun testUnescape() {
        assertEquals("<a>&'\"", unescape("&lt;a&gt;&amp;&apos;&quot;"))
        assertEquals("Hello World", unescape("Hello World"))
        assertEquals("A", unescape("&#65;"))
        assertEquals("A", unescape("&#x41;"))
        assertEquals("A", unescape("&#x0041;"))
    }

    @Test
    fun testUnescapeCustom() {
        val result =
            unescapeWith("&amp;&lt;test&gt;&baz;") { entity ->
                when (entity) {
                    "lt" -> "FOO"
                    "gt" -> "BAR"
                    "baz" -> "&lt;"
                    else -> resolveXmlEntity(entity)
                }
            }
        assertEquals("&FOOtestBAR&lt;", result)
    }

    @Test
    fun testUnescapeErrors() {
        assertFailsWith<EscapeError.UnterminatedEntity> {
            unescape("&unterminated")
        }
        assertFailsWith<EscapeError.UnrecognizedEntity> {
            unescape("&unknown;")
        }
    }

    @Test
    fun testNormalizeXml10Eols() {
        assertEquals("", normalizeXml10Eols(""))
        assertEquals("\nalready \n\n normalized\n", normalizeXml10Eols("\nalready \n\n normalized\n"))
        assertEquals("\nsome\n\ntext", normalizeXml10Eols("\r\nsome\r\n\r\ntext"))
        assertEquals("\n\u0085some\n\u0085\n\u0085text", normalizeXml10Eols("\r\u0085some\r\u0085\r\u0085text"))
        assertEquals("\u0085some\u0085\u0085text", normalizeXml10Eols("\u0085some\u0085\u0085text"))
        assertEquals("\u2028some\u2028\u2028text", normalizeXml10Eols("\u2028some\u2028\u2028text"))
        assertEquals("\n\n\n\u2028\n\nsome\n\u0085\n\u0085text", normalizeXml10Eols("\r\r\r\u2028\n\r\nsome\n\u0085\r\u0085text"))
    }

    @Test
    fun testNormalizeXml11Eols() {
        assertEquals("", normalizeXml11Eols(""))
        assertEquals("\nalready \n\n normalized\n", normalizeXml11Eols("\nalready \n\n normalized\n"))
        assertEquals("\nsome\n\ntext", normalizeXml11Eols("\r\nsome\r\n\r\ntext"))
        assertEquals("\nsome\n\ntext", normalizeXml11Eols("\r\u0085some\r\u0085\r\u0085text"))
        assertEquals("\nsome\n\ntext", normalizeXml11Eols("\u0085some\u0085\u0085text"))
        assertEquals("\nsome\n\ntext", normalizeXml11Eols("\u2028some\u2028\u2028text"))
        assertEquals("\n\n\n\n\n\nsome\n\n\ntext", normalizeXml11Eols("\r\r\r\u2028\n\r\nsome\n\u0085\r\u0085text"))
    }
}
