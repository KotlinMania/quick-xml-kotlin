// port-lint: tests utils.rs
package io.github.kotlinmania.quickxml

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {
    @Test
    fun writeByteString0() {
        val bytes = ByteBuf(byteArrayOf(10, 32, 32, 32, 32, 32, 32, 32, 32))
        assertEquals("\"0xA        \"", bytes.toString())
    }

    @Test
    fun writeByteString1() {
        val bytes =
            ByteBuf(
                byteArrayOf(
                    104,
                    116,
                    116,
                    112,
                    58,
                    47,
                    47,
                    119,
                    119,
                    119,
                    46,
                    119,
                    51,
                    46,
                    111,
                    114,
                    103,
                    47,
                    50,
                    48,
                    48,
                    50,
                    47,
                    48,
                    55,
                    47,
                    111,
                    119,
                    108,
                    35,
                ),
            )
        assertEquals("\"http://www.w3.org/2002/07/owl#\"", bytes.toString())
    }

    @Test
    fun writeByteString3() {
        val bytes =
            ByteBuf(
                byteArrayOf(
                    67,
                    108,
                    97,
                    115,
                    115,
                    32,
                    73,
                    82,
                    73,
                    61,
                    34,
                    35,
                    66,
                    34,
                ),
            )
        assertEquals("\"Class IRI=\\\"#B\\\"\"", bytes.toString())
    }

    @Test
    fun testNameLen() {
        assertEquals(0, nameLen("".encodeToByteArray()))
        assertEquals(0, nameLen(" abc".encodeToByteArray()))
        assertEquals(0, nameLen(" \t\r\n".encodeToByteArray()))

        assertEquals(3, nameLen("abc".encodeToByteArray()))
        assertEquals(3, nameLen("abc ".encodeToByteArray()))

        assertEquals(1, nameLen("a bc".encodeToByteArray()))
        assertEquals(2, nameLen("ab\tc".encodeToByteArray()))
        assertEquals(2, nameLen("ab\rc".encodeToByteArray()))
        assertEquals(2, nameLen("ab\nc".encodeToByteArray()))
    }

    @Test
    fun testTrimXmlStart() {
        assertEquals(Bytes("".encodeToByteArray()), Bytes(trimXmlStart("".encodeToByteArray())))
        assertEquals(Bytes("abc".encodeToByteArray()), Bytes(trimXmlStart("abc".encodeToByteArray())))
        assertEquals(
            Bytes("ab \t\r\nc \t\r\n".encodeToByteArray()),
            Bytes(trimXmlStart("\r\n\t ab \t\r\nc \t\r\n".encodeToByteArray())),
        )
    }

    @Test
    fun testTrimXmlEnd() {
        assertEquals(Bytes("".encodeToByteArray()), Bytes(trimXmlEnd("".encodeToByteArray())))
        assertEquals(Bytes("abc".encodeToByteArray()), Bytes(trimXmlEnd("abc".encodeToByteArray())))
        assertEquals(
            Bytes("\r\n\t ab \t\r\nc".encodeToByteArray()),
            Bytes(trimXmlEnd("\r\n\t ab \t\r\nc \t\r\n".encodeToByteArray())),
        )
    }
}
