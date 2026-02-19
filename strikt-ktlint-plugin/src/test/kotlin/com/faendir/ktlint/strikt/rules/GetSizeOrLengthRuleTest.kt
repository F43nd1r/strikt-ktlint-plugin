package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class GetSizeOrLengthRuleTest {
    private val ruleAssert = assertThatRule { GetSizeOrLengthRule() }

    @Test
    fun `should replace get(List-size) isEqualTo with hasSize`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get(List<*>::size).isEqualTo(3)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).hasSize(3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace get lambda size isEqualTo with hasSize`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get { size }.isEqualTo(3)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).hasSize(3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace get(String-length) isEqualTo with hasLength`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get(String::length).isEqualTo(5)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).hasLength(5)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace get lambda length isEqualTo with hasLength`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get { length }.isEqualTo(5)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).hasLength(5)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify get with non-size-length property`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get { name }.isEqualTo("test")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify get(size) without isEqualTo`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).get { size }.isGreaterThan(0)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
