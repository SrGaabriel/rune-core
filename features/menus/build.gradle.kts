@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    `rune-module`
    `rune-feature`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
}