@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    `rune-module`
    `rune-feature`
}

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly(rune.protocol)
}