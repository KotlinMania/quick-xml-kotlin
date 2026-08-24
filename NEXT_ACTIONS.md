# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 14/34 (41.2%)
- **Function parity:** 88/728 matched (target 300) — 12.1%
- **Class/type parity:** 27/232 matched (target 111) — 11.6%
- **Combined symbol parity:** 115/960 matched (target 411) — 12.0%
- **Average inline-code cosine:** 0.20 (function body across 8 matched files)
- **Average documentation cosine:** 0.05 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 6
- **Critical Issues:** 14 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. reader.ns_reader

- **Target:** `reader.NsReader [PROVENANCE-FALLBACK]`
- **Similarity:** 0.44
- **Dependents:** 2
- **Priority Score:** 2122605.5
- **Functions:** 13/24 matched (target 15)
- **Missing functions:** `new`, `read_event_impl`, `process_event`, `into_inner`, `get_mut`, `read_event_into`, `read_resolved_event_into`, `read_to_end_into`, `from_file`, `read_text`, `deref`
- **Types:** 1/2 matched (target 3)
- **Missing types:** `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/reader/ns_reader.rs` vs expected `reader/ns_reader.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/reader/ns_reader.rs` vs expected `reader/ns_reader.rs`
- **Proposed provenance header:** `// port-lint: source reader/ns_reader.rs` (current: `// port-lint: source tmp/quick-xml/src/reader/ns_reader.rs`)
- **Proposed provenance header:** `// port-lint: tests reader/ns_reader.rs` (current: `// port-lint: tests tmp/quick-xml/src/reader/ns_reader.rs`)
- **Lint issues:** 2

### 2. encoding

- **Target:** `quickxml.Encoding [PROVENANCE-FALLBACK]`
- **Similarity:** 0.28
- **Dependents:** 2
- **Priority Score:** 2051307.2
- **Functions:** 6/11 matched (target 12)
- **Missing functions:** `from`, `source`, `fmt`, `encoding`, `decode_into`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/encoding.rs` vs expected `encoding.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/encoding.rs` vs expected `encoding.rs`
- **Proposed provenance header:** `// port-lint: source encoding.rs` (current: `// port-lint: source tmp/quick-xml/src/encoding.rs`)
- **Proposed provenance header:** `// port-lint: tests encoding.rs` (current: `// port-lint: tests tmp/quick-xml/src/encoding.rs`)
- **Lint issues:** 2

### 3. de.mod

- **Target:** `reader.Config [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 797910.0
- **Functions:** 0/68 matched (target 2)
- **Missing functions:** `is_non_whitespace`, `new`, `trimmed`, `is_blank`, `deref`, `from`, `into_owned`, `is_empty`, `next_impl`, `current_event_is_last_text`, `drain_text`, `next`, `resolve_reference`, `read_to_end`, `decoder`, `from_str`, `from_reader`, `get_ref`, `event_buffer_size`, `peek`, `last_peeked`, `skip_whitespaces`, `skip_checkpoint`, `skip`, `skip_event`, `start_replay`, `read_string`, `read_string_impl`, `read_text`, `skip_next_tree`, `check_eof_reached`, `borrowing`, `from_str_with_resolver`, `borrowing_with_resolver`, `buffering`, `with_resolver`, `buffering_with_resolver`, `deserialize_struct`, `deserialize_unit`, `deserialize_newtype_struct`, `deserialize_enum`, `deserialize_seq`, `deserialize_option`, `deserialize_any`, `next_element_seed`, `into_deserializer`, `skip_uninterested`, `has_nil_attr`, `make_de`, `read_and_peek`, `partial_replay`, `limit`, `invalid_xml`, `complex`, `invalid_xml1`, `invalid_xml2`, `borrowing_reader_parity`, `borrowing_reader_events`, `text`, `cdata`, `text_and_cdata`, `text_and_empty_cdata`, `cdata_and_text`, `empty_cdata_and_text`, `cdata_and_cdata`, `start`, `end`, `eof`
- **Types:** 0/11 matched (target 1)
- **Missing types:** `Text`, `Target`, `DeEvent`, `PayloadEvent`, `XmlReader`, `Deserializer`, `Error`, `XmlRead`, `IoReader`, `SliceReader`, `List`
- **Tests:** 0/20 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `tmp/quick-xml/src/reader/mod.rs` vs expected `de/mod.rs`
- **Proposed provenance header:** `// port-lint: source de/mod.rs` (current: `// port-lint: source tmp/quick-xml/src/reader/mod.rs`)
- **Lint issues:** 1

### 4. events.mod

- **Target:** `events.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 676710.0
- **Functions:** 0/56 matched (target 3)
- **Missing functions:** `wrap`, `new`, `from_content`, `into_owned`, `to_owned`, `borrow`, `to_end`, `decoder`, `name`, `local_name`, `set_name`, `with_attributes`, `extend_attributes`, `push_attribute`, `clear_attributes`, `attributes`, `html_attributes`, `attributes_raw`, `try_get_attribute`, `push_attr`, `push_newline`, `push_indent`, `fmt`, `deref`, `arbitrary`, `size_hint`, `from`, `from_escaped`, `into_inner`, `decode`, `xml10_content`, `xml11_content`, `xml_content`, `html_content`, `inplace_trim_start`, `inplace_trim_end`, `escaped`, `escape`, `partial_escape`, `minimal_escape`, `next`, `target`, `content`, `from_start`, `version`, `encoding`, `standalone`, `encoder`, `is_char_ref`, `resolve_char_ref`, `as_ref`, `str_cow_to_bytes`, `trim_cow`, `bytestart_create`, `bytestart_set_name`, `bytestart_clear_attributes`
- **Types:** 0/11 matched (target 1)
- **Missing types:** `BytesStart`, `Target`, `BytesEnd`, `BytesText`, `BytesCData`, `CDataIterator`, `Item`, `BytesPI`, `BytesDecl`, `BytesRef`, `Event`
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/events/mod.rs` vs expected `events/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/events/mod.rs` vs expected `events/mod.rs`
- **Proposed provenance header:** `// port-lint: source events/mod.rs` (current: `// port-lint: source tmp/quick-xml/src/events/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests events/mod.rs` (current: `// port-lint: tests tmp/quick-xml/src/events/mod.rs`)
- **Lint issues:** 2

### 5. se.mod

- **Target:** `events.Events [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 565810.0
- **Functions:** 2/41 matched (target 72)
- **Missing functions:** `to_writer`, `to_utf8_io_writer`, `to_writer_with_root`, `to_string_with_root`, `allow_indent`, `is_text`, `is_xml11_name_start_char`, `is_xml11_name_char`, `try_from`, `borrow`, `increase`, `decrease`, `write_indent`, `with_root`, `expand_empty_elements`, `text_format`, `indent`, `set_quote_level`, `set_indent`, `ser`, `ser_name`, `serialize_none`, `serialize_some`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_seq`, `serialize_tuple`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `default_`, `minimal`, `partial`, `full`
- **Types:** 0/17 matched (target 18)
- **Missing types:** `TextFormat`, `QuoteLevel`, `WriteResult`, `XmlName`, `Indent`, `Serializer`, `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `Element`, `Example`
- **Tests:** 0/4 matched
- **Provenance warning:** port-lint provenance header matched only by basename: `tmp/quick-xml/src/events/mod.rs` vs expected `se/mod.rs`
- **Proposed provenance header:** `// port-lint: source se/mod.rs` (current: `// port-lint: source tmp/quick-xml/src/events/mod.rs`)
- **Lint issues:** 1

### 6. name

- **Target:** `quickxml.Name [PROVENANCE-FALLBACK]`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 305208.7
- **Functions:** 14/38 matched (target 66)
- **Missing functions:** `fmt`, `index`, `is_xml`, `is_xmlns`, `try_from`, `namespace`, `default`, `resolve_event`, `resolve_prefix`, `bindings_of`, `level`, `next`, `size_hint`, `basic`, `override_namespace`, `reset`, `undeclared`, `rebound_to_correct_ns`, `rebound_to_incorrect_ns`, `unbound`, `other_prefix_bound_to_xml_namespace`, `other_prefix_bound_to_xmlns_namespace`, `undeclared_prefix`, `prefix_and_local_name`
- **Types:** 8/14 matched (target 23)
- **Missing types:** `Error`, `NamespaceBinding`, `NamespaceBindingsIter`, `Item`, `PrefixIter`, `NamespaceBindingsOfLevelIter`
- **Tests:** 0/11 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/name.rs` vs expected `name.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/name.rs` vs expected `name.rs`
- **Proposed provenance header:** `// port-lint: source name.rs` (current: `// port-lint: source tmp/quick-xml/src/name.rs`)
- **Proposed provenance header:** `// port-lint: tests name.rs` (current: `// port-lint: tests tmp/quick-xml/src/name.rs`)
- **Lint issues:** 2

### 7. events.attributes

- **Target:** `events.Attributes [PROVENANCE-FALLBACK]`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 273909.5
- **Functions:** 9/31 matched (target 19)
- **Missing functions:** `decode_and_unescape_value_with`, `fmt`, `wrap`, `new`, `has_nil`, `map`, `key`, `value`, `recover`, `skip_value`, `skip_eq_value`, `check_for_duplicates`, `key_only`, `double_q`, `single_q`, `single_quoted`, `double_quoted`, `unquoted`, `key_start_invalid`, `key_contains_invalid`, `missed_value`, `mixed_quote`
- **Types:** 3/8 matched (target 9)
- **Missing types:** `Item`, `Attr`, `AttrResult`, `State`, `IterState`
- **Tests:** 0/7 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/events/attributes.rs` vs expected `events/attributes.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/events/attributes.rs` vs expected `events/attributes.rs`
- **Proposed provenance header:** `// port-lint: source events/attributes.rs` (current: `// port-lint: source tmp/quick-xml/src/events/attributes.rs`)
- **Proposed provenance header:** `// port-lint: tests events/attributes.rs` (current: `// port-lint: tests tmp/quick-xml/src/events/attributes.rs`)
- **Lint issues:** 2

### 8. reader.mod

- **Target:** `reader.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 273410.0
- **Functions:** 6/24 matched (target 28)
- **Missing functions:** `enable_all_checks`, `default`, `encoding`, `can_be_refined`, `offset`, `get_ref`, `get_mut`, `read`, `fill_buf`, `consume`, `from_reader`, `into_inner`, `stream`, `read_event_impl`, `read_until_close`, `new`, `parse`, `to_err`
- **Types:** 1/10 matched (target 2)
- **Missing types:** `Config`, `Span`, `ParseState`, `EncodingRef`, `BinaryStream`, `ReadTextResult`, `ReadRefResult`, `XmlSource`, `BangType`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/reader/mod.rs` vs expected `reader/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/reader/mod.rs` vs expected `reader/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/reader/mod.rs` vs expected `reader/mod.rs`
- **Proposed provenance header:** `// port-lint: source reader/mod.rs` (current: `// port-lint: source tmp/quick-xml/src/reader/mod.rs`)
- **Proposed provenance header:** `// port-lint: source reader/mod.rs` (current: `// port-lint: source tmp/quick-xml/src/reader/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests reader/mod.rs` (current: `// port-lint: tests tmp/quick-xml/src/reader/mod.rs`)
- **Lint issues:** 3

### 9. utils

- **Target:** `quickxml.Utils [PROVENANCE-FALLBACK]`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 203608.1
- **Functions:** 12/27 matched (target 23)
- **Missing functions:** `write_cow_string`, `deref`, `fmt`, `deserialize_str`, `deserialize_bool`, `deserialize`, `expecting`, `visit_bytes`, `visit_byte_buf`, `serialize`, `visit_borrowed_bytes`, `fill_buf`, `poll_read`, `poll_fill_buf`, `new`
- **Types:** 4/9 matched (target 5)
- **Missing types:** `CowRef`, `Target`, `ValueVisitor`, `Value`, `Item`
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/utils.rs` vs expected `utils.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/utils.rs` vs expected `utils.rs`
- **Proposed provenance header:** `// port-lint: source utils.rs` (current: `// port-lint: source tmp/quick-xml/src/utils.rs`)
- **Proposed provenance header:** `// port-lint: tests utils.rs` (current: `// port-lint: tests tmp/quick-xml/src/utils.rs`)
- **Lint issues:** 2

### 10. escape

- **Target:** `quickxml.Escape [PROVENANCE-FALLBACK]`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 172908.1
- **Functions:** 10/27 matched (target 20)
- **Missing functions:** `fmt`, `source`, `escape_char`, `normalize_xml11_eol_step`, `normalize_xml10_eol_step`, `resolve_html5_entity`, `from_str_radix`, `empty`, `already_normalized`, `cr_lf`, `cr_u0085`, `u0085`, `u2028`, `mixed`, `utf8_0xc2`, `utf8_0x0d_0xc2`, `utf8_0xe2`
- **Types:** 2/2 matched (target 10)
- **Missing types:** _none_
- **Tests:** 0/10 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/escape.rs` vs expected `escape.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/escape.rs` vs expected `escape.rs`
- **Proposed provenance header:** `// port-lint: source escape.rs` (current: `// port-lint: source tmp/quick-xml/src/escape.rs`)
- **Proposed provenance header:** `// port-lint: tests escape.rs` (current: `// port-lint: tests tmp/quick-xml/src/escape.rs`)
- **Lint issues:** 2

### 11. writer

- **Target:** `writer.Writer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 133207.0
- **Functions:** 16/27 matched (target 25)
- **Missing functions:** `get_ref`, `write_bom`, `write`, `write_serializable`, `new_line`, `write_attr`, `write_cdata_content`, `write_pi_content`, `write_str`, `additional`, `ensure`
- **Types:** 3/5 matched (target 4)
- **Missing types:** `AttributeIndent`, `ToFmtWrite`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/writer.rs` vs expected `writer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/quick-xml/src/writer.rs` vs expected `writer.rs`
- **Proposed provenance header:** `// port-lint: source writer.rs` (current: `// port-lint: source tmp/quick-xml/src/writer.rs`)
- **Proposed provenance header:** `// port-lint: tests writer.rs` (current: `// port-lint: tests tmp/quick-xml/src/writer.rs`)
- **Lint issues:** 2

### 12. errors

- **Target:** `quickxml.Errors [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 91110.0
- **Functions:** 0/5 matched (target 11)
- **Missing functions:** `fmt`, `missed_end`, `from`, `source`, `custom`
- **Types:** 2/6 matched (target 22)
- **Missing types:** `Error`, `Result`, `DeError`, `SeError`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/errors.rs` vs expected `errors.rs`
- **Proposed provenance header:** `// port-lint: source errors.rs` (current: `// port-lint: source tmp/quick-xml/src/errors.rs`)
- **Lint issues:** 1

### 13. parser.mod

- **Target:** `parser.Parser [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/parser/mod.rs` vs expected `parser/mod.rs`
- **Proposed provenance header:** `// port-lint: source parser/mod.rs` (current: `// port-lint: source tmp/quick-xml/src/parser/mod.rs`)
- **Lint issues:** 1

### 14. lib

- **Target:** `quickxml.Lib [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 4)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/quick-xml/src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source tmp/quick-xml/src/lib.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

