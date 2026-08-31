// port-lint: source quick-xml/src/events/attributes.rs
package io.github.kotlinmania.quickxml.events

import io.github.kotlinmania.quickxml.Decoder
import io.github.kotlinmania.quickxml.QName
import io.github.kotlinmania.quickxml.isWhitespace
import io.github.kotlinmania.quickxml.unescape
import io.github.kotlinmania.quickxml.unescapeWith

public sealed class AttrError : Exception() {
    public data class ExpectedEq(
        public val pos: Int,
    ) : AttrError() {
        override val message: String get() = "position " + pos + ": attribute key must be directly followed by = or space"
    }

    public data class ExpectedValue(
        public val pos: Int,
    ) : AttrError() {
        override val message: String get() = "position " + pos + ": = must be followed by an attribute value"
    }

    public data class UnquotedValue(
        public val pos: Int,
    ) : AttrError() {
        override val message: String get() = "position " + pos + ": attribute value must be enclosed in quotes"
    }

    public data class ExpectedQuote(
        public val pos: Int,
        public val quote: Byte,
    ) : AttrError() {
        override val message: String get() = "position " + pos + ": attribute value missing closing quote"
    }

    public data class Duplicated(
        public val errorPos: Int,
        public val prevPos: Int,
    ) : AttrError() {
        override val message: String get() = "position " + errorPos + ": attribute key is duplicated"
    }
}

public class Attribute(
    public val key: QName,
    public val value: ByteArray,
) {
    public constructor(key: String, value: String) : this(QName(key), value.encodeToByteArray())
    public constructor(key: ByteArray, value: ByteArray) : this(QName(key), value)

    public fun unescapeValue(): String = unescape(value.decodeToString())

    public fun unescapeValueWith(resolveEntity: (String) -> String?): String =
        unescapeWith(value.decodeToString(), resolveEntity)

    public fun decodeAndUnescapeValue(decoder: Decoder = Decoder.utf8()): String =
        unescape(decoder.decode(value))

    public fun asBool(): Boolean? {
        val s = value.decodeToString()
        return when (s) {
            "true", "1" -> true
            "false", "0" -> false
            else -> null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Attribute) return false
        return key == other.key && value.contentEquals(other.value)
    }

    override fun hashCode(): Int = 31 * key.hashCode() + value.contentHashCode()

    override fun toString(): String = key.asString() + "=\"" + value.decodeToString() + "\""

    public companion object {
        public fun from(pair: Pair<String, String>): Attribute = Attribute(pair.first, pair.second)
    }
}

public class Attributes(
    public val bytes: ByteArray,
    public var pos: Int = 0,
    public val html: Boolean = false,
    private var checkDuplicates: Boolean = true,
    private val decoder: Decoder = Decoder.utf8(),
) : Iterable<Attribute>,
    Iterator<Attribute> {
    public constructor(buf: String, pos: Int = 0) : this(buf.encodeToByteArray(), pos, false)

    private val keys = mutableListOf<IntRange>()
    private var currentOffset: Int = pos
    private var isDone: Boolean = false
    private var pending: Attribute? = null

    public fun withChecks(check: Boolean): Attributes {
        this.checkDuplicates = check
        return this
    }

    public fun decoder(): Decoder = decoder

    private fun checkDup(keyRange: IntRange) {
        if (checkDuplicates) {
            val keyBytes = bytes.copyOfRange(keyRange.first, keyRange.last + 1)
            for (prev in keys) {
                val prevBytes = bytes.copyOfRange(prev.first, prev.last + 1)
                if (keyBytes.contentEquals(prevBytes)) {
                    throw AttrError.Duplicated(keyRange.first, prev.first)
                }
            }
            keys.add(keyRange)
        }
    }

    private fun advance(): Attribute? {
        if (isDone || currentOffset >= bytes.size) {
            isDone = true
            return null
        }

        var startKey = -1
        for (i in currentOffset until bytes.size) {
            if (!isWhitespace(bytes[i])) {
                startKey = i
                break
            }
        }
        if (startKey < 0) {
            isDone = true
            return null
        }

        var keyEnd = -1
        var eqOffset = -1
        for (i in startKey until bytes.size) {
            val b = bytes[i]
            if (b == 61.toByte()) { // '='
                keyEnd = i
                eqOffset = i
                break
            } else if (isWhitespace(b)) {
                keyEnd = i
                for (j in i until bytes.size) {
                    if (!isWhitespace(bytes[j])) {
                        if (bytes[j] == 61.toByte()) {
                            eqOffset = j
                        } else {
                            if (html) {
                                currentOffset = j
                                checkDup(startKey until keyEnd)
                                val kBytes = bytes.copyOfRange(startKey, keyEnd)
                                return Attribute(QName(kBytes), byteArrayOf())
                            } else {
                                currentOffset = j
                                throw AttrError.ExpectedEq(j)
                            }
                        }
                        break
                    }
                }
                break
            }
        }

        if (keyEnd < 0) {
            isDone = true
            val e = bytes.size
            if (html) {
                checkDup(startKey until e)
                val kBytes = bytes.copyOfRange(startKey, e)
                return Attribute(QName(kBytes), byteArrayOf())
            } else {
                throw AttrError.ExpectedEq(e)
            }
        }

        if (eqOffset < 0) {
            isDone = true
            if (html) {
                checkDup(startKey until keyEnd)
                val kBytes = bytes.copyOfRange(startKey, keyEnd)
                return Attribute(QName(kBytes), byteArrayOf())
            } else {
                throw AttrError.ExpectedEq(bytes.size)
            }
        }

        val keyRange = startKey until keyEnd
        checkDup(keyRange)

        var startValue = -1
        var quote: Byte = 0
        for (i in (eqOffset + 1) until bytes.size) {
            val b = bytes[i]
            if (!isWhitespace(b)) {
                if (b == 34.toByte() || b == 39.toByte()) { // double or single quote
                    quote = b
                    startValue = i + 1
                    break
                } else if (html) {
                    var endVal = bytes.size
                    for (j in i until bytes.size) {
                        if (isWhitespace(bytes[j])) {
                            endVal = j
                            break
                        }
                    }
                    currentOffset = endVal
                    val kBytes = bytes.copyOfRange(keyRange.first, keyRange.last + 1)
                    val vBytes = bytes.copyOfRange(i, endVal)
                    return Attribute(QName(kBytes), vBytes)
                } else {
                    currentOffset = i
                    throw AttrError.UnquotedValue(i)
                }
            }
        }

        if (startValue < 0) {
            isDone = true
            throw AttrError.ExpectedValue(bytes.size)
        }

        var endValue = -1
        for (i in startValue until bytes.size) {
            if (bytes[i] == quote) {
                endValue = i
                break
            }
        }

        if (endValue < 0) {
            isDone = true
            throw AttrError.ExpectedQuote(bytes.size, quote)
        }

        currentOffset = endValue + 1
        val kBytes = bytes.copyOfRange(keyRange.first, keyRange.last + 1)
        val vBytes = bytes.copyOfRange(startValue, endValue)
        return Attribute(QName(kBytes), vBytes)
    }

    override fun hasNext(): Boolean {
        if (pending != null) return true
        if (isDone) return false
        pending = advance()
        return pending != null
    }

    override fun next(): Attribute {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        val res = pending!!
        pending = null
        return res
    }

    override fun iterator(): Iterator<Attribute> = this

    public companion object {
        public fun html(buf: String, pos: Int = 0): Attributes =
            Attributes(buf.encodeToByteArray(), pos, html = true)
    }
}
