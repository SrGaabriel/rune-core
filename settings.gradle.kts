rootProject.name = "rune-core"

dependencyResolutionManagement {
    versionCatalogs {
        create("rune") {
            from(files("rune.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include("core")
include("features")
include("features:commands")
include("features:menus")
include("features:database")
include("features:scoreboard")
include("features")
