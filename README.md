# quick-xml-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fquick--xml--kotlin-blue.svg)](https://github.com/KotlinMania/quick-xml-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/quick-xml-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/quick-xml-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/quick-xml-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/quick-xml-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`tafia/quick-xml`](https://github.com/tafia/quick-xml).

**Original Project:** This port is based on [`tafia/quick-xml`](https://github.com/tafia/quick-xml). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `tafia/quick-xml`

> The text below is reproduced and lightly edited from [`https://github.com/tafia/quick-xml`](https://github.com/tafia/quick-xml). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## quick-xml

![status](https://github.com/tafia/quick-xml/actions/workflows/rust.yml/badge.svg)
[![Crate](https://img.shields.io/crates/v/quick-xml.svg)](https://crates.io/crates/quick-xml)
[![docs.rs](https://docs.rs/quick-xml/badge.svg)](https://docs.rs/quick-xml)
[![codecov](https://img.shields.io/codecov/c/github/tafia/quick-xml)](https://codecov.io/gh/tafia/quick-xml)
[![MSRV](https://img.shields.io/badge/rustc-1.56.0+-ab6000.svg)](https://blog.rust-lang.org/2021/10/21/Rust-1.56.0.html)

High performance xml pull reader/writer.

The reader:
- is almost zero-copy (use of `Cow` whenever possible)
- is easy on memory allocation (the API provides a way to reuse buffers)
- support various encoding (with `encoding` feature), namespaces resolution, special characters.

Syntax is inspired by [xml-rs](https://github.com/netvl/xml-rs).

## Example

### Reader

```rust
use quick_xml::events::Event;
use quick_xml::reader::Reader;

let xml = r#"<tag1 att1 = "test">
                <tag2><!--Test comment-->Test</tag2>
                <tag2>Test 2</tag2>
             </tag1>"#;
let mut reader = Reader::from_str(xml);
reader.config_mut().trim_text(true);

let mut count = 0;
let mut txt = Vec::new();
let mut buf = Vec::new();

// The `Reader` does not implement `Iterator` because it outputs borrowed data (`Cow`s)
loop {
    // NOTE: this is the generic case when we don't know about the input BufRead.
    // when the input is a &str or a &[u8], we don't actually need to use another
    // buffer, we could directly call `reader.read_event()`
    match reader.read_event_into(&mut buf) {
        Err(e) => panic!("Error at position {}: {:?}", reader.error_position(), e),
        // exits the loop when reaching end of file
        Ok(Event::Eof) => break,

        Ok(Event::Start(e)) => {
            match e.name().as_ref() {
                b"tag1" => println!("attributes values: {:?}",
                                    e.attributes().map(|a| a.unwrap().value)
                                    .collect::<Vec<_>>()),
                b"tag2" => count += 1,
                _ => (),
            }
        }
        Ok(Event::Text(e)) => txt.push(e.decode().unwrap().into_owned()),

        // There are several other `Event`s we do not consider here
        _ => (),
    }
    // if we don't keep a borrow elsewhere, we can clear the buffer to keep memory usage low
    buf.clear();
}
```

### Writer

```rust
use quick_xml::events::{Event, BytesEnd, BytesStart};
use quick_xml::reader::Reader;
use quick_xml::writer::Writer;
use std::io::Cursor;

let xml = r#"<this_tag k1="v1" k2="v2"><child>text</child></this_tag>"#;
let mut reader = Reader::from_str(xml);
reader.config_mut().trim_text(true);
let mut writer = Writer::new(Cursor::new(Vec::new()));
loop {
    match reader.read_event() {
        Ok(Event::Start(e)) if e.name().as_ref() == b"this_tag" => {

            // creates a new element ... alternatively we could reuse `e` by calling
            // `e.into_owned()`
            let mut elem = BytesStart::new("my_elem");

            // collect existing attributes
            elem.extend_attributes(e.attributes().map(|attr| attr.unwrap()));

            // copy existing attributes, adds a new my-key="some value" attribute
            elem.push_attribute(("my-key", "some value"));

            // writes the event to the writer
            assert!(writer.write_event(Event::Start(elem)).is_ok());
        },
        Ok(Event::End(e)) if e.name().as_ref() == b"this_tag" => {
            assert!(writer.write_event(Event::End(BytesEnd::new("my_elem"))).is_ok());
        },
        Ok(Event::Eof) => break,
        // we can either move or borrow the event to write, depending on your use-case
        Ok(e) => assert!(writer.write_event(e).is_ok()),
        Err(e) => panic!("Error at position {}: {:?}", reader.error_position(), e),
    }
}

let result = writer.into_inner().into_inner();
let expected = r#"<my_elem k1="v1" k2="v2" my-key="some value"><child>text</child></my_elem>"#;
assert_eq!(result, expected.as_bytes());
```

## Serde

When using the `serialize` feature, quick-xml can be used with serde's `Serialize`/`Deserialize` traits.
The mapping between XML and Rust types, and in particular the syntax that allows you to specify the
distinction between *elements* and *attributes*, is described in detail in the documentation
for [deserialization](https://docs.rs/quick-xml/latest/quick_xml/de/).

### Credits

This has largely been inspired by [serde-xml-rs](https://github.com/RReverser/serde-xml-rs).
quick-xml follows its convention for deserialization, including the
[`$value`](https://github.com/RReverser/serde-xml-rs#parsing-the-value-of-a-tag) special name.

### Parsing the "value" of a tag

If you have an input of the form `<foo abc="xyz">bar</foo>`, and you want to get at the `bar`,
you can use either the special name `$text`, or the special name `$value`:

```rust,ignore
struct Foo {
    #[serde(rename = "@abc")]
    pub abc: String,
    #[serde(rename = "$text")]
    pub body: String,
}
```

Read about the difference in the [documentation](https://docs.rs/quick-xml/latest/quick_xml/de/index.html#difference-between-text-and-value-special-names).

### Performance

Note that despite not focusing on performance (there are several unnecessary copies), it remains about 10x faster than serde-xml-rs.

# Features

- `encoding`: support non utf8 xmls
- `serialize`: support serde `Serialize`/`Deserialize`

## Performance

Benchmarking is hard and the results depend on your input file and your machine.

Here on my particular file, quick-xml is around **50 times faster** than [xml-rs](https://crates.io/crates/xml-rs) crate.

```
// quick-xml benches
test bench_quick_xml            ... bench:     198,866 ns/iter (+/- 9,663)
test bench_quick_xml_escaped    ... bench:     282,740 ns/iter (+/- 61,625)
test bench_quick_xml_namespaced ... bench:     389,977 ns/iter (+/- 32,045)

// same bench with xml-rs
test bench_xml_rs               ... bench:  14,468,930 ns/iter (+/- 321,171)

// serde-xml-rs vs serialize feature
test bench_serde_quick_xml      ... bench:   1,181,198 ns/iter (+/- 138,290)
test bench_serde_xml_rs         ... bench:  15,039,564 ns/iter (+/- 783,485)
```

For a feature and performance comparison, you can also have a look at RazrFalcon's [parser comparison table](https://github.com/RazrFalcon/roxmltree#parsing).

## Contribute

Any PR is welcomed!

## License

MIT

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:quick-xml-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`tafia/quick-xml`](https://github.com/tafia/quick-xml). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the quick-xml authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`tafia/quick-xml`](https://github.com/tafia/quick-xml) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
