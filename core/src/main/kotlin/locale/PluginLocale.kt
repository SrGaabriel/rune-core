package com.runerealms.core.locale

import com.typesafe.config.Config
import org.bukkit.ChatColor

public interface PluginLocale {
    public operator fun get(key: String): String

    public fun key(key: String, vararg placeholders: String): String =
        get(key).replace(Regex("\\{\\d+}")) {
            val index = it.value.removePrefix("{").removeSuffix("}").toInt()
            placeholders.getOrNull(index) ?: it.value
        }

    public fun toMap(): Map<String, String>

    public class Builder {
        private val translations = mutableMapOf<String, String>()

        public operator fun set(key: String, value: String): Builder {
            translations[key] = value
            return this
        }

        public fun build(): PluginLocale = HashMapLocale(translations)
    }

    public companion object {
        public fun fromConfig(config: Config): PluginLocale =
            ConfigLocale(config)
    }
}

public class HashMapLocale(
    public val hashmap: MutableMap<String, String> = mutableMapOf()
): PluginLocale {
    override fun get(key: String): String = hashmap[key] ?: key

    override fun toMap(): Map<String, String> = hashmap
}

public class ConfigLocale(
    private val config: Config
): PluginLocale {
    @Suppress("Deprecation")
    override fun get(key: String): String =
        ChatColor.translateAlternateColorCodes('&', config.getString(key) ?: key)

    override fun toMap(): Map<String, String> = config.entrySet().associate { it.key to it.value.unwrapped().toString() }
}