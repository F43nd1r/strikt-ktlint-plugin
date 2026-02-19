package com.faendir.ktlint.strikt.rules

/**
 * Replaces `.hasLength(0)` with `.isEmpty()`.
 */
class HasLengthZeroRule : HasZeroRule("has-length-zero", "hasLength")
