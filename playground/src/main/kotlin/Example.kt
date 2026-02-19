import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEqualTo

fun example() {
    expectThat("hello").isEqualTo(null)
    expectThat("hello").isNotEqualTo(null)
    expectThat(true).isEqualTo(true)
    expectThat(false).isEqualTo(false)
    expectThat(listOf(1, 2, 3)).isEqualTo(listOf(1, 2, 3))
    expectThat(emptyList<String>()).isEqualTo(emptyList())
    expectThat(setOf(1, 2)).isEqualTo(setOf(1, 2))
    expectThat(emptySet<String>()).isEqualTo(emptySet())
    expectThat(emptyMap<String, String>()).isEqualTo(emptyMap())
    expectThat(listOf(1, 2)).get(List<*>::size).isEqualTo(2)
    expectThat("hello").get { length }.isEqualTo(5)
}
