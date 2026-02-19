package com.faendir.ktlint.strikt.rules

/**
 * Replaces `.not().contains(x)` with `.doesNotContain(x)`.
 */
class NotContainsRule : NotRenameRule("not-contains", "contains", "doesNotContain")
