// port-lint: source reader/mod.rs
package io.github.kotlinmania.quickxml.reader

import io.github.kotlinmania.quickxml.Decoder
import io.github.kotlinmania.quickxml.IllFormedError
import io.github.kotlinmania.quickxml.QName
import io.github.kotlinmania.quickxml.SyntaxError
import io.github.kotlinmania.quickxml.detectEncoding
import io.github.kotlinmania.quickxml.events.BytesCData
import io.github.kotlinmania.quickxml.events.BytesDecl
import io.github.kotlinmania.quickxml.events.BytesEnd
import io.github.kotlinmania.quickxml.events.BytesPI
import io.github.kotlinmania.quickxml.events.BytesRef
import io.github.kotlinmania.quickxml.events.BytesStart
import io.github.kotlinmania.quickxml.events.BytesText
import io.github.kotlinmania.quickxml.events.Event
import io.github.kotlinmania.quickxml.isWhitespace
import io.github.kotlinmania.quickxml.trimXmlEnd
import io.github.kotlinmania.quickxml.trimXmlStart

public class Reader(
    private val buffer: ByteArray,
    private var offset: Int = 0,
) {
    private val config: Config = Config()
    private val tagStack = mutableListOf<String>()
    private var pendingEnd: BytesEnd? = null
    private var decoder: Decoder = Decoder.utf8()

    public constructor(xml: String) : this(xml.encodeToByteArray())

    init {
        val detected = detectEncoding(buffer)
        if (detected != null) {
            if (detected.bomSize > 0) {
                offset = detected.bomSize
            }
            decoder = Decoder(detected.encoding)
        }
    }

    public fun config(): Config = config
    public fun configMut(): Config = config

    public fun trimText(trim: Boolean): Reader {
        config.trimText(trim)
        return this
    }

    public fun expandEmptyElements(expand: Boolean): Reader {
        config.expandEmptyElements = expand
        return this
    }

    public fun checkEndNames(check: Boolean): Reader {
        config.checkEndNames = check
        return this
    }

    public fun checkComments(check: Boolean): Reader {
        config.checkComments = check
        return this
    }

    public fun bufferPosition(): Long = offset.toLong()
    public fun errorPosition(): Long = offset.toLong()
    public fun decoder(): Decoder = decoder

    public fun readEvent(): Event {
        if (pendingEnd != null) {
            val end = pendingEnd!!
            pendingEnd = null
            return Event.End(end)
        }

        if (offset >= buffer.size) {
            return Event.Eof
        }

        val b = buffer[offset]
        if (b == 60.toByte()) { // <
            offset++
            return readMarkup()
        } else {
            return readText()
        }
    }

    private fun readText(): Event {
        val start = offset
        while (offset < buffer.size && buffer[offset] != 60.toByte()) {
            offset++
        }

        var textBytes = buffer.copyOfRange(start, offset)
        if (config.trimTextStart) {
            textBytes = trimXmlStart(textBytes)
        }
        if (config.trimTextEnd) {
            textBytes = trimXmlEnd(textBytes)
        }

        if (textBytes.isEmpty()) {
            return readEvent()
        }

        return Event.Text(BytesText(textBytes))
    }

    private fun readMarkup(): Event {
        if (offset >= buffer.size) {
            throw SyntaxError.UnclosedTag
        }

        val first = buffer[offset]
        if (first == 33.toByte()) { // !
            offset++
            return readBang()
        } else if (first == 63.toByte()) { // ?
            offset++
            return readQuestionMark()
        } else if (first == 47.toByte()) { // /
            offset++
            return readEndTag()
        } else {
            return readStartTag()
        }
    }

    private fun readBang(): Event {
        if (offset >= buffer.size) {
            throw SyntaxError.InvalidBangMarkup
        }

        if (offset + 1 < buffer.size && buffer[offset] == 45.toByte() && buffer[offset + 1] == 45.toByte()) {
            offset += 2
            val start = offset
            val endSeq = byteArrayOf(45.toByte(), 45.toByte(), 62.toByte()) // -->
            val idx = indexOfSeq(buffer, offset, endSeq)
            if (idx < 0) {
                throw SyntaxError.UnclosedComment
            }
            val content = buffer.copyOfRange(start, idx)
            offset = idx + 3
            if (config.checkComments) {
                val str = content.decodeToString()
                if (str.contains("--")) {
                    throw IllFormedError.DoubleHyphenInComment
                }
            }
            return Event.Comment(BytesText(content))
        }

        val cdataHeader = "[CDATA[".encodeToByteArray()
        if (startsWithSeq(buffer, offset, cdataHeader)) {
            offset += cdataHeader.size
            val start = offset
            val endSeq = byteArrayOf(93.toByte(), 93.toByte(), 62.toByte()) // ]]>
            val idx = indexOfSeq(buffer, offset, endSeq)
            if (idx < 0) {
                throw SyntaxError.UnclosedCData
            }
            val content = buffer.copyOfRange(start, idx)
            offset = idx + 3
            return Event.CData(BytesCData(content))
        }

        val doctypeHeader = "DOCTYPE".encodeToByteArray()
        if (startsWithSeq(buffer, offset, doctypeHeader)) {
            offset += doctypeHeader.size
            val start = offset
            var depth = 0
            var inQuotes = false
            var quoteChar: Byte = 0
            var endIdx = -1
            for (i in offset until buffer.size) {
                val b = buffer[i]
                if (inQuotes) {
                    if (b == quoteChar) {
                        inQuotes = false
                    }
                } else if (b == 34.toByte() || b == 39.toByte()) {
                    inQuotes = true
                    quoteChar = b
                } else if (b == 91.toByte()) { // [
                    depth++
                } else if (b == 93.toByte()) { // ]
                    depth--
                } else if (b == 62.toByte() && depth == 0) {
                    endIdx = i
                    break
                }
            }
            if (endIdx < 0) {
                throw SyntaxError.UnclosedDoctype
            }
            val content = buffer.copyOfRange(start, endIdx)
            offset = endIdx + 1
            val trimmed = trimXmlStart(content)
            if (trimmed.isEmpty()) {
                throw IllFormedError.MissingDoctypeName
            }
            return Event.DocType(BytesText(trimmed))
        }

        throw SyntaxError.InvalidBangMarkup
    }

    private fun readQuestionMark(): Event {
        val start = offset
        val endSeq = byteArrayOf(63.toByte(), 62.toByte()) // ?>
        val idx = indexOfSeq(buffer, offset, endSeq)
        if (idx < 0) {
            throw SyntaxError.UnclosedPIOrXmlDecl
        }
        val content = buffer.copyOfRange(start, idx)
        offset = idx + 2
        val isDecl = startsWithSeq(content, 0, "xml".encodeToByteArray()) &&
            (content.size == 3 || isWhitespace(content[3]))
        return if (isDecl) {
            Event.Decl(BytesDecl(content))
        } else {
            Event.PI(BytesPI(content))
        }
    }

    private fun readEndTag(): Event {
        val start = offset
        var endIdx = -1
        var inQuotes = false
        var quoteChar: Byte = 0
        for (i in offset until buffer.size) {
            val b = buffer[i]
            if (inQuotes) {
                if (b == quoteChar) inQuotes = false
            } else if (b == 34.toByte() || b == 39.toByte()) {
                inQuotes = true
                quoteChar = b
            } else if (b == 62.toByte()) {
                endIdx = i
                break
            }
        }
        if (endIdx < 0) {
            throw SyntaxError.UnclosedTag
        }
        var tagContent = buffer.copyOfRange(start, endIdx)
        offset = endIdx + 1
        if (config.trimMarkupNames) {
            tagContent = trimXmlStart(tagContent)
            tagContent = trimXmlEnd(tagContent)
        }
        val tagName = BytesEnd(tagContent).name().asString()
        if (config.checkEndNames) {
            if (tagStack.isEmpty()) {
                if (!config.allowUnmatchedEnds) {
                    throw IllFormedError.UnmatchedEndTag(tagName)
                }
            } else {
                val expected = tagStack.removeAt(tagStack.size - 1)
                if (expected != tagName) {
                    if (!config.allowUnmatchedEnds) {
                        throw IllFormedError.MismatchedEndTag(expected = expected, found = tagName)
                    }
                }
            }
        }
        return Event.End(BytesEnd(tagContent))
    }

    private fun readStartTag(): Event {
        val start = offset
        var endIdx = -1
        var inQuotes = false
        var quoteChar: Byte = 0
        for (i in offset until buffer.size) {
            val b = buffer[i]
            if (inQuotes) {
                if (b == quoteChar) inQuotes = false
            } else if (b == 34.toByte() || b == 39.toByte()) {
                inQuotes = true
                quoteChar = b
            } else if (b == 62.toByte()) {
                endIdx = i
                break
            }
        }
        if (endIdx < 0) {
            throw SyntaxError.UnclosedTag
        }

        val isSelfClosing = endIdx > start && buffer[endIdx - 1] == 47.toByte() // /
        val contentEnd = if (isSelfClosing) endIdx - 1 else endIdx
        val tagContent = buffer.copyOfRange(start, contentEnd)
        offset = endIdx + 1

        val elem = BytesStart(tagContent)
        val tagName = elem.name().asString()

        if (isSelfClosing) {
            if (config.expandEmptyElements) {
                pendingEnd = BytesEnd(elem.name().asRef())
                return Event.Start(elem)
            } else {
                return Event.Empty(elem)
            }
        } else {
            if (config.checkEndNames) {
                tagStack.add(tagName)
            }
            return Event.Start(elem)
        }
    }

    public fun readText(endTag: QName): String {
        val sb = StringBuilder()
        while (true) {
            when (val event = readEvent()) {
                is Event.Text -> sb.append(event.event.asString())
                is Event.CData -> sb.append(event.event.asString())
                is Event.End -> {
                    if (event.event.name() == endTag) {
                        break
                    }
                }
                is Event.Eof -> throw IllFormedError.MissingEndTag(endTag.asString())
                else -> {}
            }
        }
        return sb.toString()
    }

    public fun readToEnd(endTag: QName) {
        var depth = 1
        val endName = endTag.asString()
        while (depth > 0) {
            when (val event = readEvent()) {
                is Event.Start -> {
                    if (event.event.name().asString() == endName) {
                        depth++
                    }
                }
                is Event.End -> {
                    if (event.event.name().asString() == endName) {
                        depth--
                    }
                }
                is Event.Eof -> throw IllFormedError.MissingEndTag(endName)
                else -> {}
            }
        }
    }

    private fun indexOfSeq(src: ByteArray, start: Int, seq: ByteArray): Int {
        for (i in start..(src.size - seq.size)) {
            var match = true
            for (j in seq.indices) {
                if (src[i + j] != seq[j]) {
                    match = false
                    break
                }
            }
            if (match) return i
        }
        return -1
    }

    private fun startsWithSeq(src: ByteArray, start: Int, seq: ByteArray): Boolean {
        if (start + seq.size > src.size) return false
        for (i in seq.indices) {
            if (src[start + i] != seq[i]) return false
        }
        return true
    }

    public companion object {
        public fun fromStr(xml: String): Reader = Reader(xml.encodeToByteArray())
        public fun fromBytes(bytes: ByteArray): Reader = Reader(bytes)
    }
}
