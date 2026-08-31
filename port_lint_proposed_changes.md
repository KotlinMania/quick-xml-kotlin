# port-lint Proposed Changes

**Generated:** 2026-08-31
**Source:** tmp
**Target:** src

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `commonMain/kotlin/io/github/kotlinmania/quickxml/reader/Config.kt` | `// port-lint: source quick-xml/src/reader/mod.rs` | `// port-lint: source de/mod.rs` | `de/mod.rs` | `port-lint provenance header matched only by basename: 'quick-xml/src/reader/mod.rs' vs expected 'de/mod.rs'` |
| `commonMain/kotlin/io/github/kotlinmania/quickxml/events/Events.kt` | `// port-lint: source quick-xml/src/events/mod.rs` | `// port-lint: source se/mod.rs` | `se/mod.rs` | `port-lint provenance header matched only by basename: 'quick-xml/src/events/mod.rs' vs expected 'se/mod.rs'` |
