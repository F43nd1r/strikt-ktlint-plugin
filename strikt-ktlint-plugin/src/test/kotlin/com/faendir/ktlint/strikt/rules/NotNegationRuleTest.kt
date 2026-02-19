package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class NotNegationRuleTest {
    private val ruleAssert = assertThatRule { NotNegationRule() }

    @Test
    fun `should replace not() isNull with isNotNull`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).not().isNull()
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isNotNull()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace not() isNotNull with isNull`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).not().isNotNull()
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
    fun `should replace not() isEmpty with isNotEmpty`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).not().isEmpty()
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
    fun `should replace not() isNotEmpty with isEmpty`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).not().isNotEmpty()
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
    fun `should replace not() isBlank with isNotBlank`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).not().isBlank()
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
    fun `should replace not() isNotBlank with isBlank`() {
        ruleAssert(
            """
            fun test() {
                expectThat(str).not().isNotBlank()
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
    fun `should not modify not() with unknown assertion`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).not().isEqualTo("test")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify assertion without not()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isNull()
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
