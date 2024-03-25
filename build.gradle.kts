import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(rune.plugins.paperweight.userdev) apply false
    `maven-publish`
    idea
}

subprojects {
    group = "com.runerealms.core"
    version = Project.Version
    apply<MavenPublishPlugin>()
    apply<IdeaPlugin>()
}

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    wrapper {
        gradleVersion = "7.5.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true

        excludeDirs = excludeDirs + layout.files(
            ".idea",
            "gradle/wrapper"
        )
    }
}