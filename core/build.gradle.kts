@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    alias(rune.plugins.shadow)
    alias(rune.plugins.plugin.yml)
    alias(rune.plugins.kotlinx.seralization)
    `rune-module`
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(rune.kotlinx.datetime)
    api(rune.kotlinx.coroutines.core)
    api(rune.kotlinx.serialization.hocon)
    api(rune.cache4k)
    implementation(rune.kotlin.reflection)
    compileOnly(rune.protocol)
}

bukkit {
    name = "RuneCore"
    version = Project.Version
    main = "com.runerealms.core.CorePlugin"
    author = "SrGaabriel"
    depend = listOf("ProtocolLib")
}