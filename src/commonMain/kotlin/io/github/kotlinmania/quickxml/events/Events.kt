// port-lint: source events/mod.rs
package io.github.kotlinmania.quickxml.events

import io.github.kotlinmania.quickxml.Decoder
import io.github.kotlinmania.quickxml.LocalName
import io.github.kotlinmania.quickxml.Prefix
import io.github.kotlinmania.quickxml.QName
import io.github.kotlinmania.quickxml.escape
import io.github.kotlinmania.quickxml.isWhitespace
import io.github.kotlinmania.quickxml.nameLen
import io.github.kotlinmania.quickxml.unescape
import io.github.kotlinmania.quickxml.unescapeWith

public class BytesStart(
    public var data: ByteArray,
    public var nameLen: Int = nameLen(data),
) {
    public constructor(name: String) : this(name.encodeToByteArray())

    public fun name(): QName = QName(data.copyOfRange(0, nameLen))

    public fun localName(): LocalName = name().localName()

    public fun prefix(): Prefix? = name().prefix()

    public fun asRef(): ByteArray = data

    public fun attributes(): Attributes =
        Attributes(data, pos = nameLen, html = false)

    public fun htmlAttributes(): Attributes =
        Attributes(data, pos = nameLen, html = true)

    public fun toEnd(): BytesEnd = BytesEnd(data.copyOfRange(0, nameLen))

    public fun toBorrow(): BytesStart = BytesStart(data, nameLen)

    public fun intoOwned(): BytesStart = BytesStart(data.copyOf(), nameLen)

    public fun pushAttribute(attr: Attribute): BytesStart {
        val keyBytes = attr.key.asRef()
        val valBytes = attr.value
        val extra = 1 + keyBytes.size + 2 + valBytes.size + 1
        val newData = ByteArray(data.size + extra)
        data.copyInto(newData, 0, 0, data.size)
        var idx = data.size
        newData[idx++] = 32.toByte() // ' '
        keyBytes.copyInto(newData, idx, 0, keyBytes.size)
        idx += keyBytes.size
        newData[idx++] = 61.toByte() // '='
        newData[idx++] = 34.toByte() // '"'
        valBytes.copyInto(newData, idx, 0, valBytes.size)
        idx += valBytes.size
        newData[idx] = 34.toByte() // '"'
        this.data = newData
        return this
    }

    public fun pushAttribute(key: String, value: String): BytesStart {
        return pushAttribute(Attribute(key, value))
    }

    public fun extendAttributes(attrs: Iterable<Attribute>): BytesStart {
        for (attr in attrs) {
            pushAttribute(attr)
        }
        return this
    }

    public fun clearAttributes(): BytesStart {
        if (data.size > nameLen) {
            this.data = data.copyOfRange(0, nameLen)
        }
        return this
    }

    public fun tryGetAttribute(attrName: String): Attribute? {
        val target = attrName.encodeToByteArray()
        for (attr in attributes().withChecks(false)) {
            if (attr.key.asRef().contentEquals(target)) {
                return attr
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesStart) return false
        return nameLen == other.nameLen && data.contentEquals(other.data)
    }

    override fun hashCode(): Int = 31 * data.contentHashCode() + nameLen

    override fun toString(): String = "<${data.decodeToString()}>"

    public companion object {
        public fun from(name: String): BytesStart = BytesStart(name.encodeToByteArray())
        public fun fromContent(content: String, nameLen: Int): BytesStart = BytesStart(content.encodeToByteArray(), nameLen)
    }
}

public class BytesEnd(
    public val data: ByteArray,
    public var nameLen: Int = nameLen(data),
) {
    public constructor(name: String) : this(name.encodeToByteArray())

    public fun name(): QName = QName(data.copyOfRange(0, nameLen))

    public fun localName(): LocalName = name().localName()

    public fun prefix(): Prefix? = name().prefix()

    public fun asRef(): ByteArray = data

    public fun toBorrow(): BytesEnd = BytesEnd(data, nameLen)

    public fun intoOwned(): BytesEnd = BytesEnd(data.copyOf(), nameLen)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesEnd) return false
        return nameLen == other.nameLen && data.contentEquals(other.data)
    }

    override fun hashCode(): Int = 31 * data.contentHashCode() + nameLen

    override fun toString(): String = "</${data.decodeToString()}>"

    public companion object {
        public fun from(name: String): BytesEnd = BytesEnd(name.encodeToByteArray())
    }
}

public class BytesText(public val data: ByteArray) {
    public constructor(text: String) : this(text.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    public fun unescape(): String = unescape(data.decodeToString())

    public fun unescapeWith(resolveEntity: (String) -> String?): String =
        unescapeWith(data.decodeToString(), resolveEntity)

    public fun decodeAndUnescape(decoder: Decoder = Decoder.utf8()): String =
        unescape(decoder.decode(data))

    public fun decodeAndUnescapeWith(decoder: Decoder = Decoder.utf8(), resolveEntity: (String) -> String?): String =
        unescapeWith(decoder.decode(data), resolveEntity)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesText) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = asString()

    public companion object {
        public fun fromPlain(text: String): BytesText = BytesText(escape(text).encodeToByteArray())
        public fun fromEscaped(text: String): BytesText = BytesText(text.encodeToByteArray())
    }
}

public class BytesCData(public val data: ByteArray) {
    public constructor(content: String) : this(content.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesCData) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = "<![CDATA[${data.decodeToString()}]]>"
}

public class BytesDecl(public val data: ByteArray) {
    public constructor(content: String) : this(content.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    public fun version(): String {
        val attrs = Attributes(data, pos = 3).withChecks(false)
        for (attr in attrs) {
            if (attr.key.asString() == "version") {
                return attr.value.decodeToString()
            }
        }
        return "1.0"
    }

    public fun encoding(): String? {
        val attrs = Attributes(data, pos = 3).withChecks(false)
        for (attr in attrs) {
            if (attr.key.asString() == "encoding") {
                return attr.value.decodeToString()
            }
        }
        return null
    }

    public fun standalone(): String? {
        val attrs = Attributes(data, pos = 3).withChecks(false)
        for (attr in attrs) {
            if (attr.key.asString() == "standalone") {
                return attr.value.decodeToString()
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesDecl) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = "<?${data.decodeToString()}?>"

    public companion object {
        public fun new(version: String, encoding: String? = null, standalone: String? = null): BytesDecl {
            val sb = StringBuilder()
            sb.append("xml version=\"").append(version).append("\"")
            if (encoding != null) {
                sb.append(" encoding=\"").append(encoding).append("\"")
            }
            if (standalone != null) {
                sb.append(" standalone=\"").append(standalone).append("\"")
            }
            return BytesDecl(sb.toString())
        }
    }
}

public class BytesPI(private val raw: ByteArray) {
    public constructor(content: String) : this(content.encodeToByteArray())

    public fun intoInner(): ByteArray = raw
    public fun asRef(): ByteArray = raw
    public fun asString(): String = raw.decodeToString()

    public fun target(): QName {
        val len = nameLen(raw)
        return QName(raw.copyOfRange(0, len))
    }

    public fun data(): ByteArray {
        val len = nameLen(raw)
        var start = len
        while (start < raw.size && isWhitespace(raw[start])) {
            start++
        }
        return raw.copyOfRange(start, raw.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesPI) return false
        return raw.contentEquals(other.raw)
    }

    override fun hashCode(): Int = raw.contentHashCode()
    override fun toString(): String = "<?${raw.decodeToString()}?>"
}

public class BytesRef(public val data: ByteArray) {
    public constructor(name: String) : this(name.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesRef) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = "&${data.decodeToString()};"
}

public sealed class Event {
    public data class Start(public val event: BytesStart) : Event()
    public data class End(public val event: BytesEnd) : Event()
    public data class Empty(public val event: BytesStart) : Event()
    public data class Text(public val event: BytesText) : Event()
    public data class Comment(public val event: BytesText) : Event()
    public data class CData(public val event: BytesCData) : Event()
    public data class Decl(public val event: BytesDecl) : Event()
    public data class PI(public val event: BytesPI) : Event()
    public data class DocType(public val event: BytesText) : Event()
    public object Eof : Event() {
        override fun toString(): String = "Event.Eof"
    }
}
