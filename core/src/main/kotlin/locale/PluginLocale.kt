package com.runerealms.core.locale

public interface PluginLocale {
    public operator fun get(key: String): String

    public class Builder {
        private val translations = mutableMapOf<String, String>()

        public fun add(key: String, value: String): Builder {
            translations[key] = value
            return this
        }

        public fun build(): PluginLocale = object : PluginLocale {
            override fun get(key: String): String = translations[key] ?: key
        }
    }
}