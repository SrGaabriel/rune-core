@file:OptIn(ExperimentalSerializationApi::class)

package com.runerealms.core.config

import com.runerealms.core.RunePlugin
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig

public inline fun <reified T : Any> RunePlugin.hoconConfig(nameWithoutExtension: String, default: T): T {
    return hoconConfig(nameWithoutExtension, default, { hocon.decodeFromConfig(it) }, { hocon.encodeToConfig(it) })
}

public fun RunePlugin.hoconConfig(nameWithoutExtension: String, default: Config): Config =
    hoconConfig(nameWithoutExtension, default, { it }, { it })

public inline fun <reified T> RunePlugin.hoconConfig(
    nameWithoutExtension: String,
    default: T,
    read: (Config) -> T,
    write: (T) -> Config
): T {
    if (!dataFolder.exists()) { dataFolder.mkdirs() }
    val configFile = dataFolder.resolve("$nameWithoutExtension.conf")
    logger.info("Searching for config file '$nameWithoutExtension'...")
    return if (!configFile.exists()) {
        logger.info("Config file '$nameWithoutExtension' not found, creating...")
        configFile.createNewFile()
        configFile.writeText(write(default).root().render(DefaultRenderOptions))
        default
    } else {
        logger.info("Loading config file '$nameWithoutExtension'...")
        read(ConfigFactory.parseFile(configFile))
    }
}

@PublishedApi
internal val hocon: Hocon = Hocon {
    encodeDefaults = true
}

@PublishedApi
internal val DefaultRenderOptions: ConfigRenderOptions = ConfigRenderOptions.defaults()
    .setOriginComments(false)
    .setJson(false)
    .setFormatted(true)
    .setComments(false)
