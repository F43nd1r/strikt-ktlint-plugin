package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class EqualsMisuseRuleTest {
    private val ruleAssert = assertThatRule { EqualsMisuseRule() }

    @Test
    fun `should warn about equals() after expectThat`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).equals("hello")
            }
            """.trimIndent(),
        ).hasLintViolationWithoutAutoCorrect(
            2,
            23,
            ".equals() does not assert. Use isEqualTo() instead",
        )
    }

    @Test
    fun `should warn about equals() after chained assertion`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get { name }.equals("hello")
            }
            """.trimIndent(),
        ).hasLintViolationWithoutAutoCorrect(
            2,
            36,
            ".equals() does not assert. Use isEqualTo() instead",
        )
    }

    @Test
    fun `should warn about equals() after withNotNull`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).withNotNull { }.equals("hello")
            }
            """.trimIndent(),
        ).hasLintViolationWithoutAutoCorrect(
            2,
            39,
            ".equals() does not assert. Use isEqualTo() instead",
        )
    }

    @Test
    fun `should not warn about equals() on non-assertion receiver`() {
        ruleAssert(
            """
            fun test() {
                val result = "hello".equals("world")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
