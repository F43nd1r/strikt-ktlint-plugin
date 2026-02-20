package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class NotContainsRuleTest {
    private val ruleAssert = assertThatRule { NotContainsRule() }

    @Test
    fun `should replace not() contains with doesNotContain for non-string argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).not().contains(element)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(result).doesNotContain(element)
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
    fun `should handle chained assertions with non-string argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).isNotNull().not().contains(element)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(result).isNotNull().doesNotContain(element)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify not() contains with string argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).not().contains("placeholder")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify not() contains with chained string argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(result).isNotNull().not().contains("x")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should add import for doesNotContain`() {
        ruleAssert(
            """
            import strikt.assertions.contains

            fun test() {
                expectThat(result).not().contains(element)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.contains
            import strikt.assertions.doesNotContain

            fun test() {
                expectThat(result).doesNotContain(element)
            }
            """.trimIndent(),
        )
    }
}
