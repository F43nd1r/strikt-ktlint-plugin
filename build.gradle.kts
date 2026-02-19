plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.jgitver)
}

jgitver {
    regexVersionTag = "v([0-9]+(?:\\.[0-9]+){0,2}(?:-[a-zA-Z0-9\\-_]+)?)"
}

tasks.register("publish") {
    group = "publishing"
    subprojects {
        tasks.findByName("publish")?.let { dependsOn(it) }
    }
    dependsOn("closeAndReleaseSonatypeStagingRepository")
}

nexusPublishing {
    this.repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(project.findProperty("ossrhUser") as? String ?: System.getenv("OSSRH_USER"))
            password.set(project.findProperty("ossrhPassword") as? String ?: System.getenv("OSSRH_PASSWORD"))
        }
    }
}
