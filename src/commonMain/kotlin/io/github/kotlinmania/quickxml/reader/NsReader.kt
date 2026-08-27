// port-lint: source quick-xml/src/reader/ns_reader.rs
package io.github.kotlinmania.quickxml.reader

import io.github.kotlinmania.quickxml.Namespace
import io.github.kotlinmania.quickxml.NamespaceResolver
import io.github.kotlinmania.quickxml.PrefixBinding
import io.github.kotlinmania.quickxml.QName
import io.github.kotlinmania.quickxml.ResolveResult
import io.github.kotlinmania.quickxml.ResolvedQName
import io.github.kotlinmania.quickxml.events.Event

public data class ResolvedEvent(
    public val result: ResolveResult,
    public val event: Event,
)

public class NsReader(
    public val reader: Reader,
) {
    public constructor(xml: String) : this(Reader(xml))
    public constructor(bytes: ByteArray) : this(Reader(bytes))

    private val nsResolver: NamespaceResolver = NamespaceResolver()
    private var pendingPop: Boolean = false

    public fun config(): Config = reader.config()
    public fun configMut(): Config = reader.configMut()
    public fun resolver(): NamespaceResolver = nsResolver

    public fun prefixes(): List<PrefixBinding> = nsResolver.bindings()

    public fun resolve(name: QName, attribute: Boolean): ResolvedQName =
        nsResolver.resolve(name, !attribute)

    public fun resolveElement(name: QName): ResolvedQName =
        nsResolver.resolveElement(name)

    public fun resolveAttribute(name: QName): ResolvedQName =
        nsResolver.resolveAttribute(name)

    public fun pop() {
        if (pendingPop) {
            nsResolver.pop()
            pendingPop = false
        }
    }

    public fun readResolvedEvent(): ResolvedEvent {
        pop()
        val event = reader.readEvent()
        return when (event) {
            is Event.Start -> {
                nsResolver.push(event.event)
                val res = nsResolver.resolveElement(event.event.name()).result
                ResolvedEvent(res, event)
            }
            is Event.Empty -> {
                nsResolver.push(event.event)
                val res = nsResolver.resolveElement(event.event.name()).result
                pendingPop = true
                ResolvedEvent(res, event)
            }
            is Event.End -> {
                val res = nsResolver.resolveElement(event.event.name()).result
                pendingPop = true
                ResolvedEvent(res, event)
            }
            else -> ResolvedEvent(ResolveResult.Unbound, event)
        }
    }

    public fun readEvent(): Event {
        val resolved = readResolvedEvent()
        return resolved.event
    }

    public fun readToEnd(end: QName) {
        reader.readToEnd(end)
    }

    public companion object {
        public fun fromReader(reader: Reader): NsReader = NsReader(reader)
        public fun fromStr(s: String): NsReader = NsReader(s)
        public fun fromBytes(b: ByteArray): NsReader = NsReader(b)
    }
}
