pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("rune") {
            from(files("../rune.versions.toml"))
        }
    }
}