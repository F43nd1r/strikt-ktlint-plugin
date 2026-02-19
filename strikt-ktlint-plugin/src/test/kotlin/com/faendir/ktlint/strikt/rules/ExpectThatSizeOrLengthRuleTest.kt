package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class ExpectThatSizeOrLengthRuleTest {
    private val ruleAssert = assertThatRule { ExpectThatSizeOrLengthRule() }

    @Test
    fun `should replace expectThat(list-size) isEqualTo with hasSize`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.size).isEqualTo(3)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).hasSize(3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace expectThat(str-length) isEqualTo with hasLength`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str.length).isEqualTo(5)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(str).hasLength(5)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace expectThat with nested property access`() {
        ruleAssert(
            """
            fun test() {
                expectThat(foo.bar.size).isEqualTo(2)
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(foo.bar).hasSize(2)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify expectThat with non-size-length property`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.name).isEqualTo("test")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify expectThat(x-size) without isEqualTo`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.size).isGreaterThan(0)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify when argument is not a dot expression`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(3)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
