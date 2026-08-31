// port-lint: source quick-xml/src/reader/mod.rs
package io.github.kotlinmania.quickxml.reader

public data class Config(
    public var allowDanglingAmp: Boolean = false,
    public var allowUnmatchedEnds: Boolean = false,
    public var checkComments: Boolean = false,
    public var checkEndNames: Boolean = true,
    public var expandEmptyElements: Boolean = false,
    public var trimMarkupNamesInClosingTags: Boolean = true,
    public var trimTextStart: Boolean = false,
    public var trimTextEnd: Boolean = false,
) {
    public var trimMarkupNames: Boolean
        get() = trimMarkupNamesInClosingTags
        set(value) {
            trimMarkupNamesInClosingTags = value
        }

    public fun trimText(trim: Boolean) {
        trimTextStart = trim
        trimTextEnd = trim
    }

    public fun enableAllChecks(enable: Boolean) {
        checkComments = enable
        checkEndNames = enable
    }
}
