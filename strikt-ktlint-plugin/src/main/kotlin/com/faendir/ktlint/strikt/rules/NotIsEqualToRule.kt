package com.faendir.ktlint.strikt.rules

/**
 * Replaces `.not().isEqualTo(x)` with `.isNotEqualTo(x)`.
 */
class NotIsEqualToRule : NotRenameRule("not-is-equal-to", "isEqualTo", "isNotEqualTo")
