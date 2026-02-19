plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinter)
}

dependencies {
    implementation(libs.strikt.core)
    ktlint(project(":strikt-ktlint-plugin"))
}
