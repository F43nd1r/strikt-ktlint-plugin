package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class HasLengthZeroRuleTest {
    private val ruleAssert = assertThatRule { HasLengthZeroRule() }

    @Test
    fun `should replace hasLength(0) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).hasLength(0)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(str).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify hasLength with non-zero argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).hasLength(5)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify hasSize(0)`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).hasSize(0)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
