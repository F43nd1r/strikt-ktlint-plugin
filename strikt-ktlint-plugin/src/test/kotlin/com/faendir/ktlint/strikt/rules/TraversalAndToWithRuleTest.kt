package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class TraversalAndToWithRuleTest {
    private val ruleAssert = assertThatRule { TraversalAndToWithRule() }

    @Test
    fun `should replace single() and {} with withSingle {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).single().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).withSingle { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace first() and {} with withFirst {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).first().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).withFirst { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace last() and {} with withLast {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).last().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).withLast { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace elementAt(i) and {} with withElementAt(i) {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).elementAt(0).and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(list).withElementAt(0) { isEqualTo("hello") }
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify and {} with non-traversal receiver`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isA<String>().and { isEqualTo("hello") }
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify single() without and {}`() {
        ruleAssert(
            """
            fun test() {
                expectThat(list).single().isEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
