package com.runerealms.core.config

import com.runerealms.core.RunePlugin
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.parser.ConfigDocumentFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : Any> RunePlugin.hoconConfig(nameWithoutExtension: String, default: T): T {
    if (!dataFolder.exists()) { dataFolder.mkdirs() }
    val configFile = dataFolder.resolve("$nameWithoutExtension.conf")
    val hocon = Hocon { encodeDefaults = true }
    logger.info("Searching for config file '$nameWithoutExtension'...")
    return if (!configFile.exists()) {
        logger.info("Config file '$nameWithoutExtension' not found, creating...")
        configFile.createNewFile()
        configFile.writeText(hocon.encodeToConfig(default).root().render(DefaultRenderOptions))
        default
    } else {
        logger.info("Loading config file '$nameWithoutExtension'...")
        hocon.decodeFromConfig<T>(ConfigFactory.parseFile(configFile))
    }
}

@PublishedApi
internal val DefaultRenderOptions: ConfigRenderOptions = ConfigRenderOptions.concise()