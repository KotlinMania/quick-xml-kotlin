import Testing
import QuickXml

@Suite("QuickXml Swift Export Smoke Tests")
struct QuickXmlExportTests {
    @Test("QuickXml swift module imports cleanly")
    func swiftModuleLoads() {
        #expect(Bool(true))
    }
}

