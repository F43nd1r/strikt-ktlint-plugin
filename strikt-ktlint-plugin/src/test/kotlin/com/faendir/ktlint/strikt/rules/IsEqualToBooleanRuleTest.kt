package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsEqualToBooleanRuleTest {
    private val ruleAssert = assertThatRule { IsEqualToBooleanRule() }

    @Test
    fun `should replace isEqualTo(true) with isTrue()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(true)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isTrue()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(false) with isFalse()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(false)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isFalse()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify isEqualTo with non-boolean argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(42)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should handle chained assertions with true`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get { isActive }.isEqualTo(true)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).get { isActive }.isTrue()
            }
            """.trimIndent(),
        )
    }
}
