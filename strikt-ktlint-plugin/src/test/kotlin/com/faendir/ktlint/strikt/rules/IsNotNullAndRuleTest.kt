package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsNotNullAndRuleTest {
    private val ruleAssert = assertThatRule { IsNotNullAndRule() }

    @Test
    fun `should replace isNotNull() and {} with withNotNull {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isNotNull().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).withNotNull { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should handle chained receiver`() {
        ruleAssert(
            """
            fun test() {
                expectThat(obj).get { name }.isNotNull().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(obj).get { name }.withNotNull { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify and {} without isNotNull()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isA<String>().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isNotNull() without and {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isNotNull().isEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should add import for withNotNull`() {
        ruleAssert(
            """
            import strikt.assertions.isNotNull

            fun test() {
                expectThat(value).isNotNull().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.isNotNull
            import strikt.assertions.withNotNull

            fun test() {
                expectThat(value).withNotNull { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }
}
