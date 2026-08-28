# port-lint Proposed Changes

**Generated:** 2026-08-28
**Source:** tmp/quick-xml/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/quickxml

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/quickxml/reader/Config.kt` | `// port-lint: source reader/mod.rs` | `// port-lint: source de/mod.rs` | `de/mod.rs` | `port-lint provenance header matched only by basename: 'reader/mod.rs' vs expected 'de/mod.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/quickxml/events/Events.kt` | `// port-lint: source events/mod.rs` | `// port-lint: source se/mod.rs` | `se/mod.rs` | `port-lint provenance header matched only by basename: 'events/mod.rs' vs expected 'se/mod.rs'` |
