// port-lint: source quick-xml/src/utils.rs
package io.github.kotlinmania.quickxml

public fun writeByteString(appendable: Appendable, byteString: ByteArray) {
    appendable.append('"')
    for (b in byteString) {
        val u = b.toInt() and 0xFF
        when (u) {
            32, 33, in 35..126 -> appendable.append(u.toChar())
            34 -> appendable.append("\\\"")
            else -> {
                appendable.append("0x")
                appendable.append(u.toString(16).uppercase())
            }
        }
    }
    appendable.append('"')
}

public class ByteBuf(public val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteBuf) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String {
        val sb = StringBuilder()
        writeByteString(sb, data)
        return sb.toString()
    }
}

public class Bytes(public val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bytes) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String {
        val sb = StringBuilder()
        writeByteString(sb, data)
        return sb.toString()
    }
}

public class Fountain(
    public val chunk: ByteArray,
    public var consumed: Int = 0,
    public var overallRead: Long = 0L,
) {
    public fun read(buf: ByteArray, offset: Int = 0, length: Int = buf.size - offset): Int {
        if (chunk.isEmpty() || length <= 0) return 0
        val available = chunk.size - consumed
        val toRead = minOf(length, available)
        chunk.copyInto(buf, destinationOffset = offset, startIndex = consumed, endIndex = consumed + toRead)
        consume(toRead)
        return toRead
    }

    public fun consume(amt: Int) {
        consumed += amt
        if (consumed >= chunk.size) {
            consumed = 0
        }
        overallRead += amt.toLong()
    }
}

public fun isWhitespace(b: Byte): Boolean =
    b == ' '.code.toByte() || b == '\r'.code.toByte() || b == '\n'.code.toByte() || b == '\t'.code.toByte()

public fun isWhitespace(c: Char): Boolean =
    c == ' ' || c == '\r' || c == '\n' || c == '\t'

public fun nameLen(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size): Int {
    var len = 0
    var i = startIndex
    while (i < endIndex) {
        if (isWhitespace(bytes[i])) {
            break
        }
        len++
        i++
    }
    return len
}

public fun trimXmlStart(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size): ByteArray {
    var start = startIndex
    while (start < endIndex && isWhitespace(bytes[start])) {
        start++
    }
    return bytes.copyOfRange(start, endIndex)
}

public fun trimXmlEnd(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size): ByteArray {
    var end = endIndex
    while (end > startIndex && isWhitespace(bytes[end - 1])) {
        end--
    }
    return bytes.copyOfRange(startIndex, end)
}

public fun trimXmlSpaces(text: String): String {
    var start = 0
    var end = text.length
    while (start < end && isWhitespace(text[start])) {
        start++
    }
    while (end > start && isWhitespace(text[end - 1])) {
        end--
    }
    return if (start == 0 && end == text.length) text else text.substring(start, end)
}

public class CDataIterator(value: String) : Iterator<String> {
    private var unprocessed: String = value
    private var finished: Boolean = false

    override fun hasNext(): Boolean = !finished

    override fun next(): String {
        if (finished) {
            throw NoSuchElementException()
        }

        var gtIndex = unprocessed.indexOf('>')
        while (gtIndex >= 0) {
            val slice = unprocessed.substring(0, gtIndex)
            if (slice.endsWith("]]")) {
                unprocessed = unprocessed.substring(gtIndex)
                return slice
            }
            gtIndex = unprocessed.indexOf('>', gtIndex + 1)
        }

        finished = true
        val result = unprocessed
        unprocessed = ""
        return result
    }
}
