// port-lint: source tmp/quick-xml/src/encoding.rs
package io.github.kotlinmania.quickxml

public val UTF8_BOM: ByteArray = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
public val UTF16_LE_BOM: ByteArray = byteArrayOf(0xFF.toByte(), 0xFE.toByte())
public val UTF16_BE_BOM: ByteArray = byteArrayOf(0xFE.toByte(), 0xFF.toByte())

public object XmlEncoding {
    public const val UTF_8: String = "UTF-8"
    public const val UTF_16LE: String = "UTF-16LE"
    public const val UTF_16BE: String = "UTF-16BE"
}

public data class DetectedEncoding(
    public val encoding: String,
    public val bomSize: Int,
)

public class EncodingError(override val message: String) : Exception(message)

/**
 * Decoder of byte slices into strings.
 */
public class Decoder(public val encodingName: String = XmlEncoding.UTF_8) {
    public fun decode(bytes: ByteArray): String {
        return try {
            bytes.decodeToString()
        } catch (e: Exception) {
            throw EncodingError("cannot decode input using $encodingName: ${e.message}")
        }
    }

    public fun decodeCow(bytes: ByteArray): String = decode(bytes)

    public fun content(bytes: ByteArray, normalizeEols: (String) -> String): String {
        val decoded = decode(bytes)
        return normalizeEols(decoded)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Decoder) return false
        return encodingName.equals(other.encodingName, ignoreCase = true)
    }

    override fun hashCode(): Int = encodingName.lowercase().hashCode()
    override fun toString(): String = "Decoder($encodingName)"

    public companion object {
        public fun utf8(): Decoder = Decoder(XmlEncoding.UTF_8)
        public fun utf16(): Decoder = Decoder(XmlEncoding.UTF_16LE)
    }
}

/**
 * Automatic encoding detection of XML files based on BOM and leading bytes.
 */
public fun detectEncoding(bytes: ByteArray): DetectedEncoding? {
    if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
        return DetectedEncoding(XmlEncoding.UTF_8, 3)
    }
    if (bytes.size >= 2 && bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte()) {
        return DetectedEncoding(XmlEncoding.UTF_16BE, 2)
    }
    if (bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte()) {
        return DetectedEncoding(XmlEncoding.UTF_16LE, 2)
    }
    if (bytes.size >= 4) {
        if (bytes[0] == 0.toByte() && bytes[1] == '<'.code.toByte() && bytes[2] == 0.toByte() && bytes[3] == '?'.code.toByte()) {
            return DetectedEncoding(XmlEncoding.UTF_16BE, 0)
        }
        if (bytes[0] == '<'.code.toByte() && bytes[1] == 0.toByte() && bytes[2] == '?'.code.toByte() && bytes[3] == 0.toByte()) {
            return DetectedEncoding(XmlEncoding.UTF_16LE, 0)
        }
        if (bytes[0] == '<'.code.toByte() && bytes[1] == '?'.code.toByte() && bytes[2] == 'x'.code.toByte() && bytes[3] == 'm'.code.toByte()) {
            return DetectedEncoding(XmlEncoding.UTF_8, 0)
        }
    }
    return null
}
