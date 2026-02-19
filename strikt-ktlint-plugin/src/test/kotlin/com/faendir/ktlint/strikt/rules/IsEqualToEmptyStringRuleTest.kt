package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsEqualToEmptyStringRuleTest {
    private val ruleAssert = assertThatRule { IsEqualToEmptyStringRule() }

    @Test
    fun `should replace isEqualTo empty string with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).isEqualTo("")
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
    fun `should not modify isEqualTo with non-empty string`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).isEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isEqualTo with non-string argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(42)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
