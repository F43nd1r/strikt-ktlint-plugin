package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class ExpectCatchingToExpectThrowsRuleTest {
    private val ruleAssert = assertThatRule { ExpectCatchingToExpectThrowsRule() }

    @Test
    fun `should replace expectCatching isFailure isA with expectThrows`() {
        ruleAssert(
            """
            fun test() {
                expectCatching { doSomething() }.isFailure().isA<IllegalArgumentException>()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThrows<IllegalArgumentException> { doSomething() }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify expectCatching isFailure without isA`() {
        ruleAssert(
            """
            fun test() {
                expectCatching { doSomething() }.isFailure()
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify expectCatching isSuccess`() {
        ruleAssert(
            """
            fun test() {
                expectCatching { doSomething() }.isSuccess()
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should add import for expectThrows from strikt api`() {
        ruleAssert(
            """
            import strikt.api.expectCatching

            fun test() {
                expectCatching { doSomething() }.isFailure().isA<IllegalArgumentException>()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.api.expectCatching
            import strikt.api.expectThrows

            fun test() {
                expectThrows<IllegalArgumentException> { doSomething() }
            }
            """.trimIndent(),
        )
    }
}
