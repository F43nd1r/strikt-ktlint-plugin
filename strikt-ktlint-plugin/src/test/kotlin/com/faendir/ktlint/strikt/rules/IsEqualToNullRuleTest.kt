package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsEqualToNullRuleTest {
    private val ruleAssert = assertThatRule { IsEqualToNullRule() }

    @Test
    fun `should replace isEqualTo(null) with isNull()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isNull()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify isEqualTo with non-null argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isEqualTo with variable argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(other)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should handle chained assertions`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isA<String>().isEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isA<String>().isNull()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should add import for isNull when file has existing imports`() {
        ruleAssert(
            """
            import strikt.assertions.isEqualTo

            fun test() {
                expectThat(value).isEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.isEqualTo
            import strikt.assertions.isNull

            fun test() {
                expectThat(value).isNull()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not add import when wildcard import exists`() {
        ruleAssert(
            """
            import strikt.assertions.*

            fun test() {
                expectThat(value).isEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.*

            fun test() {
                expectThat(value).isNull()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not add duplicate import`() {
        ruleAssert(
            """
            import strikt.assertions.isEqualTo
            import strikt.assertions.isNull

            fun test() {
                expectThat(value).isEqualTo(null)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.isEqualTo
            import strikt.assertions.isNull

            fun test() {
                expectThat(value).isNull()
            }
            """.trimIndent(),
        )
    }
}
