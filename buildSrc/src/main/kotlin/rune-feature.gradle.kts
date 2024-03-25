import org.gradle.api.Project

plugins {
    kotlin("jvm")
}

val Project.sanitizedName get() =
    name.replace("-", "")

project.group = "${rootProject.group}.features.$sanitizedName"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":features"))
}