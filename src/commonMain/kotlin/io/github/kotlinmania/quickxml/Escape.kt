// port-lint: source escape.rs
package io.github.kotlinmania.quickxml

public sealed class ParseCharRefError : Exception() {
    public object UnexpectedSign : ParseCharRefError() {
        override val message: String get() = "unexpected number sign"
        override fun toString(): String = "ParseCharRefError.UnexpectedSign"
    }

    public data class InvalidNumber(public val reason: String) : ParseCharRefError() {
        override val message: String get() = reason
    }

    public data class InvalidCodepoint(public val codepoint: Long) : ParseCharRefError() {
        override val message: String get() = "`$codepoint` is not a valid codepoint"
    }

    public data class IllegalCharacter(public val codepoint: Long) : ParseCharRefError() {
        override val message: String get() = "0x${codepoint.toString(16)} character is not permitted in XML"
    }
}

public sealed class EscapeError : Exception() {
    public data class UnrecognizedEntity(
        public val range: IntRange,
        public val entity: String,
    ) : EscapeError() {
        override val message: String get() = "at $range: unrecognized entity `$entity`"
    }

    public data class UnterminatedEntity(public val range: IntRange) : EscapeError() {
        override val message: String get() = "Error while escaping character at range $range: Cannot find ';' after '&'"
    }

    public data class InvalidCharRef(public val error: ParseCharRefError) : EscapeError() {
        override val message: String get() = "invalid character reference: ${error.message}"
        override val cause: Throwable get() = error
    }
}

public fun escape(raw: String): String =
    escapeMatching(raw) { ch -> ch == '<' || ch == '>' || ch == '&' || ch == '\'' || ch == '"' }

public fun partialEscape(raw: String): String =
    escapeMatching(raw) { ch -> ch == '<' || ch == '>' || ch == '&' }

public fun minimalEscape(raw: String): String =
    escapeMatching(raw) { ch -> ch == '<' || ch == '&' }

private inline fun escapeMatching(raw: String, predicate: (Char) -> Boolean): String {
    var hasEscape = false
    for (i in 0 until raw.length) {
        if (predicate(raw[i])) {
            hasEscape = true
            break
        }
    }
    if (!hasEscape) return raw

    val sb = StringBuilder(raw.length + 16)
    var pos = 0
    for (i in 0 until raw.length) {
        val ch = raw[i]
        if (predicate(ch)) {
            sb.append(raw, pos, i)
            when (ch) {
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                '\'' -> sb.append("&apos;")
                '&' -> sb.append("&amp;")
                '"' -> sb.append("&quot;")
                '\t' -> sb.append("&#9;")
                '\n' -> sb.append("&#10;")
                '\r' -> sb.append("&#13;")
                ' ' -> sb.append("&#32;")
                else -> sb.append(ch)
            }
            pos = i + 1
        }
    }
    if (pos < raw.length) {
        sb.append(raw, pos, raw.length)
    }
    return sb.toString()
}

public fun unescape(raw: String): String =
    unescapeWith(raw, ::resolvePredefinedEntity)

public fun unescapeWith(raw: String, resolveEntity: (String) -> String?): String {
    var hasAmp = false
    for (i in 0 until raw.length) {
        if (raw[i] == '&') {
            hasAmp = true
            break
        }
    }
    if (!hasAmp) return raw

    val sb = StringBuilder(raw.length)
    var lastEnd = 0
    var i = 0
    while (i < raw.length) {
        if (raw[i] == '&') {
            val semi = raw.indexOf(';', i + 1)
            if (semi == -1) {
                throw EscapeError.UnterminatedEntity(i until raw.length)
            }
            sb.append(raw, lastEnd, i)
            val pat = raw.substring(i + 1, semi)
            if (pat.startsWith('#')) {
                val entity = pat.substring(1)
                val codepoint = parseNumber(entity)
                if (codepoint < 0x10000) {
                    sb.append(codepoint.toChar())
                } else {
                    val high = (((codepoint - 0x10000) ushr 10) + 0xD800).toChar()
                    val low = (((codepoint - 0x10000) and 0x3FF) + 0xDC00).toChar()
                    sb.append(high)
                    sb.append(low)
                }
            } else {
                val resolved = resolveEntity(pat)
                    ?: throw EscapeError.UnrecognizedEntity(i + 1 until semi, pat)
                sb.append(resolved)
            }
            lastEnd = semi + 1
            i = semi + 1
        } else {
            i++
        }
    }
    if (lastEnd < raw.length) {
        sb.append(raw, lastEnd, raw.length)
    }
    return sb.toString()
}

public fun resolveXmlEntity(entity: String): String? =
    when (entity) {
        "lt" -> "<"
        "gt" -> ">"
        "amp" -> "&"
        "apos" -> "'"
        "quot" -> "\""
        else -> null
    }

public fun resolvePredefinedEntity(entity: String): String? =
    resolveXmlEntity(entity)

public fun parseNumber(num: String): Int {
    val code = if (num.startsWith('x') || num.startsWith('X')) {
        val hex = num.substring(1)
        if (hex.isEmpty()) throw ParseCharRefError.InvalidNumber("empty hex string")
        if (hex.startsWith('+') || hex.startsWith('-')) throw ParseCharRefError.UnexpectedSign
        try {
            hex.toLong(16)
        } catch (e: Exception) {
            throw ParseCharRefError.InvalidNumber(e.message ?: "invalid hex number")
        }
    } else {
        if (num.isEmpty()) throw ParseCharRefError.InvalidNumber("empty decimal string")
        if (num.startsWith('+') || num.startsWith('-')) throw ParseCharRefError.UnexpectedSign
        try {
            num.toLong(10)
        } catch (e: Exception) {
            throw ParseCharRefError.InvalidNumber(e.message ?: "invalid decimal number")
        }
    }

    if (code == 0L) {
        throw ParseCharRefError.IllegalCharacter(code)
    }
    if (code > 0x10FFFFL || (code in 0xD800L..0xDFFFL)) {
        throw ParseCharRefError.InvalidCodepoint(code)
    }
    return code.toInt()
}

public fun normalizeXml10Eols(text: String): String {
    if (!text.contains('\r')) return text
    val sb = StringBuilder(text.length)
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        if (ch == '\r') {
            sb.append('\n')
            if (i + 1 < text.length && text[i + 1] == '\n') {
                i += 2
            } else {
                i += 1
            }
        } else {
            sb.append(ch)
            i += 1
        }
    }
    return sb.toString()
}

public fun normalizeXml11Eols(text: String): String {
    var needsNorm = false
    for (i in 0 until text.length) {
        val ch = text[i]
        if (ch == '\r' || ch == '\u0085' || ch == '\u2028') {
            needsNorm = true
            break
        }
    }
    if (!needsNorm) return text

    val sb = StringBuilder(text.length)
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        when (ch) {
            '\r' -> {
                sb.append('\n')
                if (i + 1 < text.length && (text[i + 1] == '\n' || text[i + 1] == '\u0085')) {
                    i += 2
                } else {
                    i += 1
                }
            }
            '\u0085', '\u2028' -> {
                sb.append('\n')
                i += 1
            }
            else -> {
                sb.append(ch)
                i += 1
            }
        }
    }
    return sb.toString()
}
