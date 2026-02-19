# Strikt Ktlint Plugin

A Ktlint plugin that autofixes [Strikt](https://strikt.io/) assertion style by replacing generic assertions with their more specific equivalents.

## Rules

### Autocorrecting Rules

All autocorrecting rules work with or without explicit type arguments (e.g. both `listOf(1, 2)` and `listOf<Int>(1, 2)`).

| Rule ID | Before | After |
|---|---|---|
| `is-equal-to-null` | `.isEqualTo(null)` | `.isNull()` |
| `is-not-equal-to-null` | `.isNotEqualTo(null)` | `.isNotNull()` |
| `is-equal-to-boolean` | `.isEqualTo(true)` / `.isEqualTo(false)` | `.isTrue()` / `.isFalse()` |
| `is-equal-to-collection` | `.isEqualTo(listOf(a, b))` | `.containsExactly(a, b)` |
|  | `.isEqualTo(setOf(a, b))` | `.containsExactlyInAnyOrder(a, b)` |
| `is-equal-to-empty-collection` | `.isEqualTo(emptyList())` / `.isEqualTo(listOf())` | `.isEmpty()` |
|  | `.isEqualTo(emptySet())` / `.isEqualTo(setOf())` | `.isEmpty()` |
|  | `.isEqualTo(emptyMap())` / `.isEqualTo(mapOf())` | `.isEmpty()` |
| `is-equal-to-empty-string` | `.isEqualTo("")` | `.isEmpty()` |
| `get-size-or-length` | `.get(X::size).isEqualTo(n)` / `.get { size }.isEqualTo(n)` | `.hasSize(n)` |
|  | `.get(X::length).isEqualTo(n)` / `.get { length }.isEqualTo(n)` | `.hasLength(n)` |
| `expect-that-size-or-length` | `expectThat(x.size).isEqualTo(n)` | `expectThat(x).hasSize(n)` |
|  | `expectThat(x.length).isEqualTo(n)` | `expectThat(x).hasLength(n)` |
| `has-size-zero` | `.hasSize(0)` | `.isEmpty()` |
| `has-length-zero` | `.hasLength(0)` | `.isEmpty()` |
| `not-is-equal-to` | `.not().isEqualTo(x)` | `.isNotEqualTo(x)` |
| `not-contains` | `.not().contains(x)` | `.doesNotContain(x)` |
| `not-negation` | `.not().isNull()` | `.isNotNull()` |
|  | `.not().isEmpty()` | `.isNotEmpty()` |
|  | `.not().isBlank()` | `.isNotBlank()` |
| `expect-that-boolean-method` | `expectThat(x.isEmpty()).isTrue()` | `expectThat(x).isEmpty()` |
|  | `expectThat(x.isEmpty()).isFalse()` | `expectThat(x).isNotEmpty()` |
| `is-not-null-and` | `.isNotNull().and { }` | `.withNotNull { }` |
| `expect-catching-to-expect-throws` | `expectCatching { }.isFailure().isA<E>()` | `expectThrows<E> { }` |
| `traversal-and-to-with` | `.single().and { }` | `.withSingle { }` |
|  | `.first().and { }` | `.withFirst { }` |
|  | `.last().and { }` | `.withLast { }` |
|  | `.elementAt(i).and { }` | `.withElementAt(i) { }` |

### Warning-Only Rules

| Rule ID | Description |
|---|---|
| `equals-misuse` | Flags `.equals(x)` calls on assertion builders as likely bugs (should be `.isEqualTo(x)` instead) |

## Usage

### Add the Plugin
Add the plugin to ktlint. How to do this depends on how you call ktlint.

Below are some examples:

#### [Kotlinter](https://github.com/jeremymailen/kotlinter-gradle)

```kotlin
plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jmailen.kotlinter") version "<kotlinter-version>"
}

dependencies {
    ktlint("com.faendir.ktlint:strikt-ktlint-plugin:<version>")
}
```

#### [ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle)

```kotlin
plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jlleitschuh.gradle.ktlint") version "<ktlint-gradle-version>"
}

dependencies {
    ktlintRuleset("com.faendir.ktlint:strikt-ktlint-plugin:<version>")
}
```

#### [Ktlint CLI](https://pinterest.github.io/ktlint/latest/quick-start/)
```sh
ktlint -R strikt-ktlint-plugin.jar --relative test.kt
```
