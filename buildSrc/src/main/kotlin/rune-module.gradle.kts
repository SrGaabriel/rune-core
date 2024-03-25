import com.runerealms.core.build.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
}

kotlin {
    explicitApi()
}

withCatalog("rune") {
    apply(plugin = pluginId("paperweight.userdev"))
    dependencies {
        compileOnly(library("purpur"))
        "paperweightDevelopmentBundle"(library("purpur.dev.bundle"))
    }
}

tasks {
    publishing {
        val sourcesJar by registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        val docsJar by registering(Jar::class) {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Javadocs"
            archiveExtension.set("javadoc")
            from(javadoc)
            dependsOn(javadoc)
        }

        publications {
            create<MavenPublication>("Rune") {
                groupId = rootProject.group.toString()
                version = Project.Version
                artifactId = "rune-${project.name}"

                from(components["java"])
                artifact(sourcesJar.get())
                artifact(docsJar.get())

                pom {
                    name.set("Rune")
                }
            }
        }
    }
}