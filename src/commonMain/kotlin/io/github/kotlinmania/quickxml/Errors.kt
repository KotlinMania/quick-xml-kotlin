// port-lint: source tmp/quick-xml/src/errors.rs
package io.github.kotlinmania.quickxml

import io.github.kotlinmania.quickxml.events.AttrError

public sealed class SyntaxError : Exception() {
    public object UnclosedTag : SyntaxError() {
        override val message: String get() = "unclosed tag"
        override fun toString(): String = "SyntaxError.UnclosedTag"
    }

    public object UnclosedComment : SyntaxError() {
        override val message: String get() = "unclosed comment"
        override fun toString(): String = "SyntaxError.UnclosedComment"
    }

    public object UnclosedCData : SyntaxError() {
        override val message: String get() = "unclosed CDATA"
        override fun toString(): String = "SyntaxError.UnclosedCData"
    }

    public object UnclosedPIOrXmlDecl : SyntaxError() {
        override val message: String get() = "unclosed processing instruction or XML declaration"
        override fun toString(): String = "SyntaxError.UnclosedPIOrXmlDecl"
    }

    public object UnclosedDoctype : SyntaxError() {
        override val message: String get() = "unclosed DOCTYPE declaration"
        override fun toString(): String = "SyntaxError.UnclosedDoctype"
    }

    public object InvalidBangMarkup : SyntaxError() {
        override val message: String get() = "invalid bang markup"
        override fun toString(): String = "SyntaxError.InvalidBangMarkup"
    }
}

public sealed class IllFormedError : Exception() {
    public object DoubleHyphenInComment : IllFormedError() {
        override val message: String get() = "comment contains '--'"
        override fun toString(): String = "IllFormedError.DoubleHyphenInComment"
    }

    public object MissingDoctypeName : IllFormedError() {
        override val message: String get() = "missing DOCTYPE name"
        override fun toString(): String = "IllFormedError.MissingDoctypeName"
    }

    public data class MissingEndTag(public val name: String) : IllFormedError() {
        override val message: String get() = "missing closing tag for '$name'"
        override fun toString(): String = "IllFormedError.MissingEndTag($name)"
    }

    public data class UnmatchedEndTag(public val name: String) : IllFormedError() {
        override val message: String get() = "unmatched closing tag '$name'"
        override fun toString(): String = "IllFormedError.UnmatchedEndTag($name)"
    }

    public data class MismatchedEndTag(public val expected: String, public val found: String) : IllFormedError() {
        override val message: String get() = "mismatched closing tag: expected '$expected', found '$found'"
        override fun toString(): String = "IllFormedError.MismatchedEndTag(expected=$expected, found=$found)"
    }
}

public sealed class XmlException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    public class Io(public val reason: String, cause: Throwable? = null) :
        XmlException("I/O error: $reason", cause)

    public class Syntax(public val error: SyntaxError) :
        XmlException("syntax error: ${error.message}", error)

    public class IllFormed(public val error: IllFormedError) :
        XmlException("ill-formed document: ${error.message}", error)

    public class InvalidAttr(public val error: AttrError) :
        XmlException("error while parsing attribute: ${error.message}", error)

    public class Encoding(public val error: EncodingError) :
        XmlException("encoding error: " + error.message, error)

    public class Escape(public val error: EscapeError) :
        XmlException("escape error: " + error.message, error)

    public class Namespace(public val error: NamespaceError) :
        XmlException("namespace error: " + error.message, error)
}

public typealias QuickXmlException = XmlException
