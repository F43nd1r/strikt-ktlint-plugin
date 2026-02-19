package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsEqualToEmptyCollectionRuleTest {
    private val ruleAssert = assertThatRule { IsEqualToEmptyCollectionRule() }

    @Test
    fun `should replace isEqualTo(emptyList()) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(emptyList<String>())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(listOf()) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf<String>())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(emptySet()) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(emptySet<String>())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(setOf()) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(setOf<String>())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(emptyMap()) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(emptyMap<String, String>())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(mapOf()) with isEmpty()`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(mapOf<String, String>())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(emptyList()) without type arguments`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(emptyList())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(listOf()) without type arguments`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(emptySet()) without type arguments`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(emptySet())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(emptyMap()) without type arguments`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(emptyMap())
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).isEmpty()
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify isEqualTo(listOf(elements))`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf(1, 2, 3))
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isEqualTo with non-collection argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo("hello")
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }
}
