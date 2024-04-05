package com.runerealms.core.locale

import com.typesafe.config.Config

public interface PluginLocale {
    public operator fun get(key: String): String

    public fun key(key: String, vararg placeholders: String): String =
        get(key).replace(Regex("\\{\\d+}")) {
            val index = it.value.removePrefix("{").removeSuffix("}").toInt()
            placeholders.getOrNull(index) ?: it.value
        }

    public fun toMap(): Map<String, String> = emptyMap()

    public class Builder {
        private val translations = mutableMapOf<String, String>()

        public operator fun set(key: String, value: String): Builder {
            translations[key] = value
            return this
        }

        public fun build(): PluginLocale = object : PluginLocale {
            override fun get(key: String): String = translations[key] ?: key
        }
    }

    public companion object {
        public fun fromConfig(config: Config): PluginLocale {
            val translations = mutableMapOf<String, String>()
            for ((key, value) in config.root().unwrapped()) {
                translations[key] = value.toString().replace('&', '§')
            }
            return object : PluginLocale {
                override fun get(key: String): String = translations[key] ?: key
            }
        }
    }
}