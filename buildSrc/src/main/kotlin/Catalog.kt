package com.runerealms.core.build

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

inline fun Project.withCatalog(name: String, crossinline block: VersionCatalog.() -> Unit) {
    val libs = extensions.getByType<VersionCatalogsExtension>()
    pluginManager.withPlugin("java") {
        val catalog = libs.named(name)
        catalog.block()
    }
}

fun VersionCatalog.version(name: String) = findVersion(name).get().displayName

fun VersionCatalog.library(id: String) = findLibrary(id).get().get().run { "$module:${versionConstraint.displayName}" }

fun VersionCatalog.pluginId(id: String) = findPlugin(id).get().get().pluginId