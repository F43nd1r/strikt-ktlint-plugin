package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsNotEqualToNullRuleTest {
    private val ruleAssert = assertThatRule { IsNotEqualToNullRule() }

    @Test
    fun `should replace isNotEqualTo(null) with isNotNull()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isNotEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isNotNull()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify isNotEqualTo with non-null argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isNotEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should handle chained assertions`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isA<String>().isNotEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isA<String>().isNotNull()
            }
            """.trimIndent(),
        )
    }
}
