// port-lint: source parser/mod.rs
package io.github.kotlinmania.quickxml.parser

import io.github.kotlinmania.quickxml.SyntaxError

/**
 * Used to decouple reading of data from data source and parsing XML structure from it.
 */
public interface Parser {
    /**
     * Process new data and try to determine end of the parsed thing.
     * Returns position of the end of thing in `bytes` in case of successful search and `null` otherwise.
     */
    public fun feed(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Int?

    /**
     * Returns parse error produced by this parser in case of reaching end of input without finding the end of a parsed thing.
     */
    public fun eofError(): SyntaxError
}

/**
 * A parser that searches a `>` symbol in the slice outside of quoted regions.
 */
public class ElementParser(
    public var state: State = State.Outside,
) : Parser {
    public enum class State {
        Outside,
        SingleQ,
        DoubleQ,
    }

    override fun feed(bytes: ByteArray, offset: Int, length: Int): Int? {
        var i = offset
        val end = offset + length
        while (i < end) {
            val b = bytes[i]
            when (state) {
                State.Outside -> {
                    when (b) {
                        '>'.code.toByte() -> return i
                        '\''.code.toByte() -> state = State.SingleQ
                        '"'.code.toByte() -> state = State.DoubleQ
                    }
                }
                State.SingleQ -> {
                    if (b == '\''.code.toByte()) {
                        state = State.Outside
                    }
                }
                State.DoubleQ -> {
                    if (b == '"'.code.toByte()) {
                        state = State.Outside
                    }
                }
            }
            i++
        }
        return null
    }

    override fun eofError(): SyntaxError = SyntaxError.UnclosedTag
}

/**
 * A parser that searches a `?>` sequence in the slice.
 */
public class PiParser(
    public var hadQuestionMark: Boolean = false,
) : Parser {
    override fun feed(bytes: ByteArray, offset: Int, length: Int): Int? {
        if (length <= 0) return null
        val end = offset + length
        var i = offset

        while (i < end) {
            if (bytes[i] == '>'.code.toByte()) {
                if (i == offset && hadQuestionMark) {
                    return i
                } else if (i > offset && bytes[i - 1] == '?'.code.toByte()) {
                    return i
                }
            }
            i++
        }
        hadQuestionMark = bytes[end - 1] == '?'.code.toByte()
        return null
    }

    override fun eofError(): SyntaxError = SyntaxError.UnclosedPIOrXmlDecl
}
