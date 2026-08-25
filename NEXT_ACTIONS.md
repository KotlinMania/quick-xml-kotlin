# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 12/34 (35.3%)
- **Function parity:** 80/619 matched (target 204) — 12.9%
- **Class/type parity:** 26/204 matched (target 91) — 12.7%
- **Combined symbol parity:** 106/823 matched (target 295) — 12.9%
- **Average inline-code cosine:** 0.29 (function body across 9 matched files)
- **Average documentation cosine:** 0.04 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 11 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. reader.ns_reader

- **Target:** `reader.NsReader`
- **Similarity:** 0.44
- **Dependents:** 2
- **Priority Score:** 2122605.5
- **Functions:** 13/24 matched (target 15)
- **Missing functions:** `new`, `read_event_impl`, `process_event`, `into_inner`, `get_mut`, `read_event_into`, `read_resolved_event_into`, `read_to_end_into`, `from_file`, `read_text`, `deref`
- **Types:** 1/2 matched (target 3)
- **Missing types:** `Target`

### 2. encoding

- **Target:** `quickxml.Encoding`
- **Similarity:** 0.28
- **Dependents:** 2
- **Priority Score:** 2051307.2
- **Functions:** 6/11 matched (target 12)
- **Missing functions:** `from`, `source`, `fmt`, `encoding`, `decode_into`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_

### 3. events.mod

- **Target:** `events.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 676710.0
- **Functions:** 0/56 matched (target 3)
- **Missing functions:** `wrap`, `new`, `from_content`, `into_owned`, `to_owned`, `borrow`, `to_end`, `decoder`, `name`, `local_name`, `set_name`, `with_attributes`, `extend_attributes`, `push_attribute`, `clear_attributes`, `attributes`, `html_attributes`, `attributes_raw`, `try_get_attribute`, `push_attr`, `push_newline`, `push_indent`, `fmt`, `deref`, `arbitrary`, `size_hint`, `from`, `from_escaped`, `into_inner`, `decode`, `xml10_content`, `xml11_content`, `xml_content`, `html_content`, `inplace_trim_start`, `inplace_trim_end`, `escaped`, `escape`, `partial_escape`, `minimal_escape`, `next`, `target`, `content`, `from_start`, `version`, `encoding`, `standalone`, `encoder`, `is_char_ref`, `resolve_char_ref`, `as_ref`, `str_cow_to_bytes`, `trim_cow`, `bytestart_create`, `bytestart_set_name`, `bytestart_clear_attributes`
- **Types:** 0/11 matched (target 1)
- **Missing types:** `BytesStart`, `Target`, `BytesEnd`, `BytesText`, `BytesCData`, `CDataIterator`, `Item`, `BytesPI`, `BytesDecl`, `BytesRef`, `Event`
- **Tests:** 0/3 matched

### 4. reader.mod

- **Target:** `reader.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 343410.0
- **Functions:** 0/24 matched (target 6)
- **Missing functions:** `trim_text`, `enable_all_checks`, `default`, `encoding`, `can_be_refined`, `offset`, `get_ref`, `get_mut`, `read`, `fill_buf`, `consume`, `from_reader`, `config`, `config_mut`, `into_inner`, `buffer_position`, `error_position`, `decoder`, `stream`, `read_event_impl`, `read_until_close`, `new`, `parse`, `to_err`
- **Types:** 0/10 matched (target 1)
- **Missing types:** `Config`, `Span`, `ParseState`, `EncodingRef`, `BinaryStream`, `Reader`, `ReadTextResult`, `ReadRefResult`, `XmlSource`, `BangType`

### 5. name

- **Target:** `quickxml.Name`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 305208.7
- **Functions:** 14/38 matched (target 66)
- **Missing functions:** `fmt`, `index`, `is_xml`, `is_xmlns`, `try_from`, `namespace`, `default`, `resolve_event`, `resolve_prefix`, `bindings_of`, `level`, `next`, `size_hint`, `basic`, `override_namespace`, `reset`, `undeclared`, `rebound_to_correct_ns`, `rebound_to_incorrect_ns`, `unbound`, `other_prefix_bound_to_xml_namespace`, `other_prefix_bound_to_xmlns_namespace`, `undeclared_prefix`, `prefix_and_local_name`
- **Types:** 8/14 matched (target 23)
- **Missing types:** `Error`, `NamespaceBinding`, `NamespaceBindingsIter`, `Item`, `PrefixIter`, `NamespaceBindingsOfLevelIter`
- **Tests:** 0/11 matched

### 6. events.attributes

- **Target:** `events.Attributes`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 273909.5
- **Functions:** 9/31 matched (target 19)
- **Missing functions:** `decode_and_unescape_value_with`, `fmt`, `wrap`, `new`, `has_nil`, `map`, `key`, `value`, `recover`, `skip_value`, `skip_eq_value`, `check_for_duplicates`, `key_only`, `double_q`, `single_q`, `single_quoted`, `double_quoted`, `unquoted`, `key_start_invalid`, `key_contains_invalid`, `missed_value`, `mixed_quote`
- **Types:** 3/8 matched (target 9)
- **Missing types:** `Item`, `Attr`, `AttrResult`, `State`, `IterState`
- **Tests:** 0/7 matched

### 7. utils

- **Target:** `quickxml.Utils`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 203608.1
- **Functions:** 12/27 matched (target 23)
- **Missing functions:** `write_cow_string`, `deref`, `fmt`, `deserialize_str`, `deserialize_bool`, `deserialize`, `expecting`, `visit_bytes`, `visit_byte_buf`, `serialize`, `visit_borrowed_bytes`, `fill_buf`, `poll_read`, `poll_fill_buf`, `new`
- **Types:** 4/9 matched (target 5)
- **Missing types:** `CowRef`, `Target`, `ValueVisitor`, `Value`, `Item`
- **Tests:** 3/3 matched

### 8. escape

- **Target:** `quickxml.Escape`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 172908.1
- **Functions:** 10/27 matched (target 20)
- **Missing functions:** `fmt`, `source`, `escape_char`, `normalize_xml11_eol_step`, `normalize_xml10_eol_step`, `resolve_html5_entity`, `from_str_radix`, `empty`, `already_normalized`, `cr_lf`, `cr_u0085`, `u0085`, `u2028`, `mixed`, `utf8_0xc2`, `utf8_0x0d_0xc2`, `utf8_0xe2`
- **Types:** 2/2 matched (target 10)
- **Missing types:** _none_
- **Tests:** 0/10 matched

### 9. writer

- **Target:** `writer.Writer`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 133207.0
- **Functions:** 16/27 matched (target 25)
- **Missing functions:** `get_ref`, `write_bom`, `write`, `write_serializable`, `new_line`, `write_attr`, `write_cdata_content`, `write_pi_content`, `write_str`, `additional`, `ensure`
- **Types:** 3/5 matched (target 4)
- **Missing types:** `AttributeIndent`, `ToFmtWrite`

### 10. errors

- **Target:** `quickxml.Errors`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 91110.0
- **Functions:** 0/5 matched (target 11)
- **Missing functions:** `fmt`, `missed_end`, `from`, `source`, `custom`
- **Types:** 2/6 matched (target 22)
- **Missing types:** `Error`, `Result`, `DeError`, `SeError`

### 11. parser.mod

- **Target:** `parser.Parser [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 4)
- **Missing types:** _none_

### 12. lib

- **Target:** `quickxml.Lib`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 4)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `de.mod` | `de.Mod` | 0 | `de/mod.rs` | `de/Mod.kt` |
| `se.mod` | `se.Mod` | 0 | `se/mod.rs` | `se/Mod.kt` |

