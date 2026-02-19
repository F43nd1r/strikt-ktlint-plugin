package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class NotIsEqualToRuleTest {
    private val ruleAssert = assertThatRule { NotIsEqualToRule() }

    @Test
    fun `should replace not() isEqualTo with isNotEqualTo`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).not().isEqualTo("hello")
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isNotEqualTo("hello")
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify isEqualTo without not()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should handle chained assertions`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isA<String>().not().isEqualTo("hello")
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isA<String>().isNotEqualTo("hello")
            }
            """.trimIndent(),
        )
    }
}
