@Suppress("DSL_SCOPE_VIOLATION")
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
    compileOnly(kotlin("stdlib"))
    api(rune.exposed.core)
    api(rune.exposed.jdbc)
    api(rune.exposed.dao)
    api(rune.exposed.datetime)
    api(rune.kotlinx.datetime)
    implementation(rune.hikaricp)
}

tasks {
    shadowJar {
        exclude("kotlin/**")
        minimize()
    }
}