package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class ExpectThatBooleanMethodRuleTest {
    private val ruleAssert = assertThatRule { ExpectThatBooleanMethodRule() }

    @Test
    fun `should replace expectThat(x isEmpty()) isTrue() with expectThat(x) isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.isEmpty()).isTrue()
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
    fun `should replace expectThat(x isEmpty()) isFalse() with expectThat(x) isNotEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.isEmpty()).isFalse()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).isNotEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace expectThat(x isNotEmpty()) isTrue() with expectThat(x) isNotEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.isNotEmpty()).isTrue()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).isNotEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace expectThat(x isBlank()) isTrue() with expectThat(x) isBlank()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str.isBlank()).isTrue()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(str).isBlank()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace expectThat(x isBlank()) isFalse() with expectThat(x) isNotBlank()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str.isBlank()).isFalse()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(str).isNotBlank()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify when inner method is unknown`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str.startsWith("x")).isTrue()
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify when assertion is not isTrue or isFalse`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list.isEmpty()).isEqualTo(true)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify when argument is not a dot expression`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isTrue()
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
