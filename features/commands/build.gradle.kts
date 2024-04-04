@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    alias(rune.plugins.shadow)
    `rune-module`
    `rune-feature`
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(rune.brigadier)
    implementation(rune.commodore)
    implementation(rune.adventure.kotlin)
}

tasks {
    shadowJar {
        exclude("kotlin/**")
    }
}