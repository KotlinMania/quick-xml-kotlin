// port-lint: source tmp/quick-xml/src/name.rs
package io.github.kotlinmania.quickxml

public sealed class NamespaceError : Exception() {
    public data class UnknownPrefix(public val prefix: ByteArray) : NamespaceError() {
        override val message: String get() = "unknown namespace prefix '${prefix.decodeToString()}'"
        override fun equals(other: Any?): Boolean = other is UnknownPrefix && prefix.contentEquals(other.prefix)
        override fun hashCode(): Int = prefix.contentHashCode()
    }

    public data class InvalidXmlPrefixBind(public val namespace: ByteArray) : NamespaceError() {
        override val message: String get() = "the namespace prefix 'xml' cannot be bound to '${namespace.decodeToString()}'"
        override fun equals(other: Any?): Boolean = other is InvalidXmlPrefixBind && namespace.contentEquals(other.namespace)
        override fun hashCode(): Int = namespace.contentHashCode()
    }

    public data class InvalidXmlnsPrefixBind(public val namespace: ByteArray) : NamespaceError() {
        override val message: String get() = "the namespace prefix 'xmlns' cannot be bound to '${namespace.decodeToString()}'"
        override fun equals(other: Any?): Boolean = other is InvalidXmlnsPrefixBind && namespace.contentEquals(other.namespace)
        override fun hashCode(): Int = namespace.contentHashCode()
    }

    public data class InvalidPrefixForXml(public val prefix: ByteArray) : NamespaceError() {
        override val message: String get() = "the namespace prefix '${prefix.decodeToString()}' cannot be bound to 'http://www.w3.org/XML/1998/namespace'"
        override fun equals(other: Any?): Boolean = other is InvalidPrefixForXml && prefix.contentEquals(other.prefix)
        override fun hashCode(): Int = prefix.contentHashCode()
    }

    public data class InvalidPrefixForXmlns(public val prefix: ByteArray) : NamespaceError() {
        override val message: String get() = "the namespace prefix '${prefix.decodeToString()}' cannot be bound to 'http://www.w3.org/2000/xmlns/'"
        override fun equals(other: Any?): Boolean = other is InvalidPrefixForXmlns && prefix.contentEquals(other.prefix)
        override fun hashCode(): Int = prefix.contentHashCode()
    }
}

public data class DecomposedName(
    public val localName: LocalName,
    public val prefix: Prefix?,
)

public class QName(public val data: ByteArray) {
    public constructor(str: String) : this(str.encodeToByteArray())

    private val colonIndex: Int = data.indexOf(58.toByte()) // ':'

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data

    public fun localName(): LocalName =
        if (colonIndex < 0) {
            LocalName(data)
        } else {
            LocalName(data.copyOfRange(colonIndex + 1, data.size))
        }

    public fun prefix(): Prefix? =
        if (colonIndex < 0) {
            null
        } else {
            Prefix(data.copyOfRange(0, colonIndex))
        }

    public fun decompose(): DecomposedName =
        if (colonIndex < 0) {
            DecomposedName(LocalName(data), null)
        } else {
            DecomposedName(LocalName(data.copyOfRange(colonIndex + 1, data.size)), Prefix(data.copyOfRange(0, colonIndex)))
        }

    public fun asNamespaceBinding(): PrefixDeclaration? {
        val colon = colonIndex
        if (colon < 0) {
            return if (data.contentEquals(XMLNS_BYTES)) {
                PrefixDeclaration.Default
            } else {
                null
            }
        }
        val prefixPart = data.copyOfRange(0, colon)
        return if (prefixPart.contentEquals(XMLNS_BYTES)) {
            val namePart = data.copyOfRange(colon + 1, data.size)
            PrefixDeclaration.Named(namePart.decodeToString())
        } else {
            null
        }
    }

    public fun asString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QName) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = asString()

    public companion object {
        private val XMLNS_BYTES = "xmlns".encodeToByteArray()

        public fun from(str: String): QName = QName(str.encodeToByteArray())
        public fun from(bytes: ByteArray): QName = QName(bytes)
    }
}

public class LocalName(public val data: ByteArray) {
    public constructor(str: String) : this(str.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalName) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = asString()

    public companion object {
        public fun from(str: String): LocalName = LocalName(str.encodeToByteArray())
        public fun from(bytes: ByteArray): LocalName = LocalName(bytes)
    }
}

public class Prefix(public val data: ByteArray) {
    public constructor(str: String) : this(str.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Prefix) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = asString()

    public companion object {
        public fun from(str: String): Prefix = Prefix(str.encodeToByteArray())
        public fun from(bytes: ByteArray): Prefix = Prefix(bytes)
    }
}

public class Namespace(public val data: ByteArray) {
    public constructor(str: String) : this(str.encodeToByteArray())

    public fun intoInner(): ByteArray = data
    public fun asRef(): ByteArray = data
    public fun asString(): String = data.decodeToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Namespace) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
    override fun toString(): String = asString()

    public companion object {
        public val XML: Namespace = Namespace("http://www.w3.org/XML/1998/namespace".encodeToByteArray())
        public val XMLNS: Namespace = Namespace("http://www.w3.org/2000/xmlns/".encodeToByteArray())

        public fun from(str: String): Namespace = Namespace(str.encodeToByteArray())
        public fun from(bytes: ByteArray): Namespace = Namespace(bytes)
    }
}

public sealed class PrefixDeclaration {
    public object Default : PrefixDeclaration() {
        override fun toString(): String = "PrefixDeclaration.Default"
    }

    public data class Named(public val prefix: String) : PrefixDeclaration() {
        override fun toString(): String = "PrefixDeclaration.Named($prefix)"
    }
}

public data class PrefixBinding(
    public val prefix: PrefixDeclaration,
    public val namespace: Namespace,
)

public sealed class ResolveResult {
    public data class Bound(public val namespace: Namespace) : ResolveResult()
    public data class Unknown(public val prefix: String) : ResolveResult()
    public object Unbound : ResolveResult() {
        override fun toString(): String = "ResolveResult.Unbound"
    }
}

public data class ResolvedQName(
    public val result: ResolveResult,
    public val localName: LocalName,
)

public class NamespaceResolver {
    public companion object {
        public val RESERVED_XML_NS: Namespace = Namespace("http://www.w3.org/XML/1998/namespace")
        public val RESERVED_XMLNS_NS: Namespace = Namespace("http://www.w3.org/2000/xmlns/")
    }

    private val scopes = mutableListOf<Int>()
    private val bindingsList = mutableListOf<Pair<String?, Namespace?>>()

    init {
        bindingsList.add("xml" to RESERVED_XML_NS)
        bindingsList.add("xmlns" to RESERVED_XMLNS_NS)
    }

    public fun push() {
        scopes.add(bindingsList.size)
    }

    internal fun pushAttributes(attributes: List<Pair<QName, ByteArray>>) {
        scopes.add(bindingsList.size)
        for ((name, value) in attributes) {
            val nameStr = name.asString()
            if (nameStr == "xmlns") {
                val ns = if (value.isEmpty()) null else Namespace(value)
                bindingsList.add(null to ns)
            } else if (nameStr.startsWith("xmlns:")) {
                val prefix = nameStr.substring(6)
                val ns = if (value.isEmpty()) null else Namespace(value)
                bindingsList.add(prefix to ns)
            }
        }
    }

    public fun push(start: io.github.kotlinmania.quickxml.events.BytesStart) {
        val list = mutableListOf<Pair<QName, ByteArray>>()
        for (attr in start.attributes().withChecks(false)) {
            list.add(attr.key to attr.value)
        }
        pushAttributes(list)
    }

    public fun pop() {
        if (scopes.isNotEmpty()) {
            val targetSize = scopes.removeAt(scopes.size - 1)
            while (bindingsList.size > targetSize) {
                bindingsList.removeAt(bindingsList.size - 1)
            }
        }
    }

    public fun bindings(): List<PrefixBinding> {
        val list = mutableListOf<PrefixBinding>()
        for (i in 2 until bindingsList.size) {
            val (p, ns) = bindingsList[i]
            if (ns != null) {
                val decl = if (p == null) PrefixDeclaration.Default else PrefixDeclaration.Named(p)
                list.add(PrefixBinding(decl, ns))
            }
        }
        return list
    }

    public fun add(declaration: PrefixDeclaration, namespace: Namespace) {
        when (declaration) {
            is PrefixDeclaration.Default -> {
                bindingsList.add(null to namespace)
            }
            is PrefixDeclaration.Named -> {
                if (declaration.prefix == "xml") {
                    if (namespace != RESERVED_XML_NS) {
                        throw NamespaceError.InvalidXmlPrefixBind(namespace.data)
                    }
                } else if (declaration.prefix == "xmlns") {
                    throw NamespaceError.InvalidXmlnsPrefixBind(namespace.data)
                } else {
                    if (namespace == RESERVED_XML_NS) {
                        throw NamespaceError.InvalidPrefixForXml(declaration.prefix.encodeToByteArray())
                    }
                    if (namespace == RESERVED_XMLNS_NS) {
                        throw NamespaceError.InvalidPrefixForXmlns(declaration.prefix.encodeToByteArray())
                    }
                    bindingsList.add(declaration.prefix to namespace)
                }
            }
        }
    }

    public fun resolve(name: QName, useDefault: Boolean = true): ResolvedQName {
        val (local, prefixObj) = name.decompose()
        val prefix = prefixObj?.asString()

        if (prefix == null) {
            if (!useDefault) {
                return ResolvedQName(ResolveResult.Unbound, local)
            }
            for (i in bindingsList.size - 1 downTo 0) {
                val (p, ns) = bindingsList[i]
                if (p == null) {
                    return if (ns != null && ns.data.isNotEmpty()) {
                        ResolvedQName(ResolveResult.Bound(ns), local)
                    } else {
                        ResolvedQName(ResolveResult.Unbound, local)
                    }
                }
            }
            return ResolvedQName(ResolveResult.Unbound, local)
        }

        for (i in bindingsList.size - 1 downTo 0) {
            val (p, ns) = bindingsList[i]
            if (p == prefix) {
                return if (ns != null && ns.data.isNotEmpty()) {
                    ResolvedQName(ResolveResult.Bound(ns), local)
                } else {
                    ResolvedQName(ResolveResult.Unbound, local)
                }
            }
        }
        return ResolvedQName(ResolveResult.Unknown(prefix), local)
    }

    public fun resolveElement(name: QName): ResolvedQName =
        resolve(name, useDefault = true)

    public fun resolveAttribute(name: QName): ResolvedQName =
        resolve(name, useDefault = false)
}

public typealias Namespaces = NamespaceResolver
