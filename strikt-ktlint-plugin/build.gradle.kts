plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
}

dependencies {
    implementation(libs.ktlint.core)

    testImplementation(libs.ktlint.test)
    testImplementation(libs.ktlint.rule.engine)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.slf4j)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    group = "documentation"
    from(tasks["dokkaGenerate"])
    archiveClassifier.set("javadoc")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    group = "documentation"
    from(project.extensions.getByType<SourceSetContainer>()["main"].allSource)
    archiveClassifier.set("sources")
}

val githubUser: String by lazy { project.findProperty("githubUser") as? String ?: System.getenv("GITHUB_USER") ?: "" }
val githubPassword: String by lazy { project.findProperty("githubPackageKey") as? String ?: System.getenv("GITHUB_TOKEN") ?: "" }
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.findByName("java"))

            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name.set("strikt-ktlint-plugin")
                description.set("Ktlint plugin to autofix strikt assertion style.")
                url.set("https://github.com/F43nd1r/strikt-ktlint-plugin")
                scm {
                    connection.set("scm:git:https://github.com/F43nd1r/strikt-ktlint-plugin.git")
                    developerConnection.set("scm:git:git@github.com:F43nd1r/strikt-ktlint-plugin.git")
                    url.set("https://github.com/F43nd1r/strikt-ktlint-plugin.git")
                }
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("f43nd1r")
                        name.set("Lukas Morawietz")
                    }
                }
            }
        }
    }
    repositories {
        mavenLocal()
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/F43nd1r/strikt-ktlint-plugin")
            credentials {
                username = githubUser
                password = githubPassword
            }
        }
    }
}

signing {
    val signingKey = project.findProperty("signingKey") as? String ?: System.getenv("SIGNING_KEY")
    val signingPassword = project.findProperty("signingPassword") as? String ?: System.getenv("SIGNING_PASSWORD")
    isRequired = signingKey != null
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}
