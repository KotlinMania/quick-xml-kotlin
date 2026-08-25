// port-lint: tests reader/ns_reader.rs
package io.github.kotlinmania.quickxml

import io.github.kotlinmania.quickxml.events.Event
import io.github.kotlinmania.quickxml.reader.NsReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NsReaderTest {
    @Test
    fun testNamespaceResolution() {
        val xml = "<root xmlns=\"http://default.ns\" xmlns:p=\"http://prefix.ns\"><p:child/></root>"
        val reader = NsReader.fromStr(xml)

        val (r1, e1) = reader.readResolvedEvent()
        assertTrue(r1 is ResolveResult.Bound)
        assertEquals("http://default.ns", r1.namespace.asString())
        assertTrue(e1 is Event.Start)

        val (r2, e2) = reader.readResolvedEvent()
        assertTrue(r2 is ResolveResult.Bound)
        assertEquals("http://prefix.ns", r2.namespace.asString())
        assertTrue(e2 is Event.Empty)

        val (r3, e3) = reader.readResolvedEvent()
        assertTrue(r3 is ResolveResult.Bound)
        assertEquals("http://default.ns", r3.namespace.asString())
        assertTrue(e3 is Event.End)
    }
}
