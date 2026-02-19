package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class HasSizeZeroRuleTest {
    private val ruleAssert = assertThatRule { HasSizeZeroRule() }

    @Test
    fun `should replace hasSize(0) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).hasSize(0)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify hasSize with non-zero argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).hasSize(3)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify hasLength(0)`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).hasLength(0)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should handle chained assertions`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).isNotNull().hasSize(0)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).isNotNull().isEmpty()
            }
            """.trimIndent(),
        )
    }
}
