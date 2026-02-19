package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class NotContainsRuleTest {
    private val ruleAssert = assertThatRule { NotContainsRule() }

    @Test
    fun `should replace not() contains with doesNotContain`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).not().contains("placeholder")
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(result).doesNotContain("placeholder")
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify contains without not()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).contains("placeholder")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should handle chained assertions`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).isNotNull().not().contains("x")
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(result).isNotNull().doesNotContain("x")
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should add import for doesNotContain`() {
        ruleAssert(
            """
            import strikt.assertions.contains

            fun test() {
                expectThat(result).not().contains("placeholder")
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.contains
            import strikt.assertions.doesNotContain

            fun test() {
                expectThat(result).doesNotContain("placeholder")
            }
            """.trimIndent(),
        )
    }
}
