// port-lint: tests tmp/quick-xml/src/encoding.rs
package io.github.kotlinmania.quickxml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EncodingTest {
    @Test
    fun testDetectEncodingWithBom() {
        val utf8WithBom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), '<'.code.toByte(), '?'.code.toByte())
        val detected = detectEncoding(utf8WithBom)
        assertNotNull(detected)
        assertEquals(XmlEncoding.UTF_8, detected.encoding)
        assertEquals(3, detected.bomSize)

        val utf16LeWithBom = byteArrayOf(0xFF.toByte(), 0xFE.toByte(), '<'.code.toByte(), 0)
        val detectedLe = detectEncoding(utf16LeWithBom)
        assertNotNull(detectedLe)
        assertEquals(XmlEncoding.UTF_16LE, detectedLe.encoding)
        assertEquals(2, detectedLe.bomSize)

        val utf16BeWithBom = byteArrayOf(0xFE.toByte(), 0xFF.toByte(), 0, '<'.code.toByte())
        val detectedBe = detectEncoding(utf16BeWithBom)
        assertNotNull(detectedBe)
        assertEquals(XmlEncoding.UTF_16BE, detectedBe.encoding)
        assertEquals(2, detectedBe.bomSize)
    }

    @Test
    fun testDetectEncodingWithoutBom() {
        val utf8Xml = "<?xml version=\"1.0\"?>".encodeToByteArray()
        val detected = detectEncoding(utf8Xml)
        assertNotNull(detected)
        assertEquals(XmlEncoding.UTF_8, detected.encoding)
        assertEquals(0, detected.bomSize)
    }

    @Test
    fun testDecoder() {
        val decoder = Decoder.utf8()
        val text = "hello world <xml>"
        assertEquals(text, decoder.decode(text.encodeToByteArray()))
    }
}
