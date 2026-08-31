// port-lint: tests quick-xml/src/name.rs
package io.github.kotlinmania.quickxml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NameTest {
    @Test
    fun testUnprefixedBasic() {
        val name = QName("simple")
        val ns = Namespace("default")
        val resolver = NamespaceResolver()

        resolver.push()
        resolver.add(PrefixDeclaration.Default, ns)
        assertEquals(ResolveResult.Bound(ns), resolver.resolve(name, true).result)
        assertEquals(LocalName("simple"), resolver.resolve(name, true).localName)
        assertEquals(ResolveResult.Unbound, resolver.resolve(name, false).result)
        assertEquals(LocalName("simple"), resolver.resolve(name, false).localName)
    }

    @Test
    fun testOverrideNamespace() {
        val name = QName("simple")
        val oldNs = Namespace("old")
        val newNs = Namespace("new")
        val resolver = NamespaceResolver()

        resolver.push()
        resolver.add(PrefixDeclaration.Default, oldNs)
        resolver.push()
        resolver.add(PrefixDeclaration.Default, newNs)

        assertEquals(ResolveResult.Bound(newNs), resolver.resolve(name, true).result)
        assertEquals(ResolveResult.Unbound, resolver.resolve(name, false).result)

        resolver.pop()
        assertEquals(ResolveResult.Bound(oldNs), resolver.resolve(name, true).result)
        assertEquals(ResolveResult.Unbound, resolver.resolve(name, false).result)
    }

    @Test
    fun testReset() {
        val name = QName("simple")
        val oldNs = Namespace("old")
        val resolver = NamespaceResolver()

        resolver.push()
        resolver.add(PrefixDeclaration.Default, oldNs)
        resolver.push()
        resolver.add(PrefixDeclaration.Default, Namespace(""))

        assertEquals(ResolveResult.Unbound, resolver.resolve(name, true).result)
        assertEquals(ResolveResult.Unbound, resolver.resolve(name, false).result)

        resolver.pop()
        assertEquals(ResolveResult.Bound(oldNs), resolver.resolve(name, true).result)
        assertEquals(ResolveResult.Unbound, resolver.resolve(name, false).result)
    }

    @Test
    fun testDeclaredPrefixBasic() {
        val name = QName("p:with-declared-prefix")
        val ns = Namespace("default")
        val resolver = NamespaceResolver()

        resolver.push()
        resolver.add(PrefixDeclaration.Named("p"), ns)

        assertEquals(ResolveResult.Bound(ns), resolver.resolve(name, true).result)
        assertEquals(LocalName("with-declared-prefix"), resolver.resolve(name, true).localName)
        assertEquals(ResolveResult.Bound(ns), resolver.resolve(name, false).result)
        assertEquals(LocalName("with-declared-prefix"), resolver.resolve(name, false).localName)
    }

    @Test
    fun testUndeclaredPrefix() {
        val name = QName("unknown:prefix")
        val resolver = NamespaceResolver()

        assertEquals(ResolveResult.Unknown("unknown"), resolver.resolve(name, true).result)
        assertEquals(LocalName("prefix"), resolver.resolve(name, true).localName)
        assertEquals(ResolveResult.Unknown("unknown"), resolver.resolve(name, false).result)
        assertEquals(LocalName("prefix"), resolver.resolve(name, false).localName)
    }

    @Test
    fun testPrefixAndLocalName() {
        val name1 = QName("foo:bus")
        assertEquals(Prefix("foo"), name1.prefix())
        assertEquals(LocalName("bus"), name1.localName())
        assertEquals(DecomposedName(LocalName("bus"), Prefix("foo")), name1.decompose())

        val name2 = QName("foo:")
        assertEquals(Prefix("foo"), name2.prefix())
        assertEquals(LocalName(""), name2.localName())
        assertEquals(DecomposedName(LocalName(""), Prefix("foo")), name2.decompose())

        val name3 = QName(":foo")
        assertEquals(Prefix(""), name3.prefix())
        assertEquals(LocalName("foo"), name3.localName())
        assertEquals(DecomposedName(LocalName("foo"), Prefix("")), name3.decompose())

        val name4 = QName("foo:bus:baz")
        assertEquals(Prefix("foo"), name4.prefix())
        assertEquals(LocalName("bus:baz"), name4.localName())
        assertEquals(DecomposedName(LocalName("bus:baz"), Prefix("foo")), name4.decompose())
    }

    @Test
    fun testBuiltinXml() {
        val name = QName("xml:random")
        val resolver = NamespaceResolver()
        assertEquals(ResolveResult.Bound(NamespaceResolver.RESERVED_XML_NS), resolver.resolve(name, true).result)

        assertFailsWith<NamespaceError.InvalidXmlPrefixBind> {
            resolver.add(PrefixDeclaration.Named("xml"), Namespace("not_correct_namespace"))
        }
        assertFailsWith<NamespaceError.InvalidPrefixForXml> {
            resolver.add(PrefixDeclaration.Named("not_xml"), NamespaceResolver.RESERVED_XML_NS)
        }
    }

    @Test
    fun testBuiltinXmlns() {
        val name = QName("xmlns:random")
        val resolver = NamespaceResolver()
        assertEquals(ResolveResult.Bound(NamespaceResolver.RESERVED_XMLNS_NS), resolver.resolve(name, true).result)

        assertFailsWith<NamespaceError.InvalidXmlnsPrefixBind> {
            resolver.add(PrefixDeclaration.Named("xmlns"), NamespaceResolver.RESERVED_XMLNS_NS)
        }
        assertFailsWith<NamespaceError.InvalidPrefixForXmlns> {
            resolver.add(PrefixDeclaration.Named("not_xmlns"), NamespaceResolver.RESERVED_XMLNS_NS)
        }
    }
}
