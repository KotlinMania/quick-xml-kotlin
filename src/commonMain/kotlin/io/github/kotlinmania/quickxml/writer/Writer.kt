// port-lint: source tmp/quick-xml/src/writer.rs
package io.github.kotlinmania.quickxml.writer

import io.github.kotlinmania.quickxml.events.Attribute
import io.github.kotlinmania.quickxml.events.BytesStart
import io.github.kotlinmania.quickxml.events.BytesText
import io.github.kotlinmania.quickxml.events.Event

public class Indentation(
    public val indentChar: Char = ' ',
    public val indentSize: Int = 4,
) {
    public var depth: Int = 0
    public var shouldLineBreak: Boolean = false

    public fun current(): String = indentChar.toString().repeat(depth * indentSize)

    public fun grow() {
        depth++
    }

    public fun shrink() {
        if (depth > 0) {
            depth--
        }
    }
}

public class Writer(
    private val output: Appendable = StringBuilder(),
    private val indent: Indentation? = null,
) {
    public fun getMut(): Appendable = output

    public fun writeEvent(event: Event): Writer {
        var nextShouldLineBreak = true
        when (event) {
            is Event.Start -> {
                writeWrapped("<", event.event.asRef().decodeToString(), ">")
                indent?.grow()
            }
            is Event.End -> {
                indent?.shrink()
                writeWrapped("</", event.event.asRef().decodeToString(), ">")
            }
            is Event.Empty -> {
                writeWrapped("<", event.event.asRef().decodeToString(), "/>")
            }
            is Event.Text -> {
                nextShouldLineBreak = false
                output.append(event.event.asString())
            }
            is Event.Comment -> {
                writeWrapped("<!--", event.event.asString(), "-->")
            }
            is Event.CData -> {
                nextShouldLineBreak = false
                output.append("<![CDATA[")
                output.append(event.event.asString())
                output.append("]]>")
            }
            is Event.Decl -> {
                writeWrapped("<?", event.event.asString(), "?>")
            }
            is Event.PI -> {
                writeWrapped("<?", event.event.asString(), "?>")
            }
            is Event.DocType -> {
                writeWrapped("<!DOCTYPE ", event.event.asString(), ">")
            }
            is Event.Eof -> {}
        }
        indent?.shouldLineBreak = nextShouldLineBreak
        return this
    }

    private fun writeWrapped(before: String, content: String, after: String) {
        if (indent != null && indent.shouldLineBreak) {
            output.append("\n")
            output.append(indent.current())
        }
        output.append(before)
        output.append(content)
        output.append(after)
    }

    public fun writeIndent(): Writer {
        if (indent != null) {
            output.append("\n")
            output.append(indent.current())
        }
        return this
    }

    public fun createElement(name: String): ElementWriter =
        ElementWriter(this, BytesStart(name))

    public fun intoInner(): Appendable = output
    public fun intoString(): String = output.toString()

    override fun toString(): String = output.toString()

    public companion object {
        public fun new(output: Appendable = StringBuilder()): Writer = Writer(output)

        public fun newWithIndent(output: Appendable, indentChar: Char, indentSize: Int): Writer =
            Writer(output, Indentation(indentChar, indentSize))

        public fun newWithIndent(output: Appendable, indentSize: Int = 4): Writer =
            Writer(output, Indentation(' ', indentSize))
    }
}

public class ElementWriter(
    private val writer: Writer,
    private val startTag: BytesStart,
) {
    public fun withAttribute(attr: Attribute): ElementWriter {
        startTag.pushAttribute(attr)
        return this
    }

    public fun withAttribute(key: String, value: String): ElementWriter {
        startTag.pushAttribute(Attribute(key, value))
        return this
    }

    public fun withAttributes(attrs: Iterable<Attribute>): ElementWriter {
        for (attr in attrs) {
            startTag.pushAttribute(attr)
        }
        return this
    }

    public fun writeEmpty() {
        writer.writeEvent(Event.Empty(startTag))
    }

    public fun writeTextContent(text: BytesText) {
        writer.writeEvent(Event.Start(startTag))
        writer.writeEvent(Event.Text(text))
        writer.writeEvent(Event.End(startTag.toEnd()))
    }

    public fun writeTextContent(text: String) {
        writeTextContent(BytesText.fromPlain(text))
    }

    public fun writeInnerContent(block: (Writer) -> Unit) {
        writer.writeEvent(Event.Start(startTag))
        block(writer)
        writer.writeEvent(Event.End(startTag.toEnd()))
    }
}
