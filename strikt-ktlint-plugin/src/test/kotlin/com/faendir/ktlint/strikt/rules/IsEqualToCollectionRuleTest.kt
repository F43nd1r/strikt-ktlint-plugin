package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import org.junit.jupiter.api.Test

class IsEqualToCollectionRuleTest {
    private val ruleAssert = assertThatRule { IsEqualToCollectionRule() }

    @Test
    fun `should replace isEqualTo(listOf(elements)) with containsExactly(elements)`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf(1, 2, 3))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).containsExactly(1, 2, 3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(setOf(elements)) with containsExactlyInAnyOrder(elements)`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(setOf(1, 2, 3))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).containsExactlyInAnyOrder(1, 2, 3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(listOf(single)) with containsExactly(single)`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf("hello"))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).containsExactly("hello")
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(listOf(elements)) with explicit type arguments`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf<Int>(1, 2, 3))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).containsExactly(1, 2, 3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should replace isEqualTo(setOf(elements)) with explicit type arguments`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(setOf<String>("a", "b"))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            fun test() {
                expectThat(value).containsExactlyInAnyOrder("a", "b")
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should not modify isEqualTo(listOf()) with type args - empty list handled by other rule`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf<String>())
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isEqualTo(listOf()) without type args - empty list handled by other rule`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(listOf())
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isEqualTo with non-collection argument`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(42)
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should not modify isEqualTo(mapOf(elements))`() {
        ruleAssert(
            """
            fun test() {
                expectThat(value).isEqualTo(mapOf("a" to 1))
            }
            """.trimIndent(),
        ).hasNoLintViolations()
    }

    @Test
    fun `should add import for containsExactly`() {
        ruleAssert(
            """
            import strikt.assertions.isEqualTo

            fun test() {
                expectThat(value).isEqualTo(listOf(1, 2, 3))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.isEqualTo
            import strikt.assertions.containsExactly

            fun test() {
                expectThat(value).containsExactly(1, 2, 3)
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `should add import for containsExactlyInAnyOrder`() {
        ruleAssert(
            """
            import strikt.assertions.isEqualTo

            fun test() {
                expectThat(value).isEqualTo(setOf(1, 2))
            }
            """.trimIndent(),
        ).isFormattedAs(
            """
            import strikt.assertions.isEqualTo
            import strikt.assertions.containsExactlyInAnyOrder

            fun test() {
                expectThat(value).containsExactlyInAnyOrder(1, 2)
            }
            """.trimIndent(),
        )
    }
}
