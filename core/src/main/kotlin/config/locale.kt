package com.runerealms.core.config

import com.runerealms.core.RunePlugin
import com.runerealms.core.locale.PluginLocale
import com.typesafe.config.ConfigFactory

public fun RunePlugin.createLocale(nameWithoutExtension: String, builder: PluginLocale.Builder.() -> Unit): PluginLocale {
    val locale = PluginLocale.Builder().apply(builder).build()
    val default = ConfigFactory.parseMap(locale.toMap())
    val parsed = hoconConfig(nameWithoutExtension, default)
    val result = PluginLocale.fromConfig(parsed)
    this.localeOrNull = result
    return result
}