@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    alias(rune.plugins.shadow)
    `rune-module`
    `rune-feature`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
    api(rune.exposed.core)
    api(rune.exposed.jdbc)
    api(rune.exposed.dao)
    api(rune.kotlinx.datetime)
    api(rune.exposed.datetime)
    implementation(rune.hikaricp)
}

tasks {
    build {
        dependsOn("reobfJar")
    }
}