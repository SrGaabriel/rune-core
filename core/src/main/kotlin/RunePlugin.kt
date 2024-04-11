package com.runerealms.core

import com.runerealms.core.feature.RuneFeature
import com.runerealms.core.feature.RuneFeatureInstance
import com.runerealms.core.lifecycle.ILifecycle
import com.runerealms.core.locale.PluginLocale
import org.bukkit.plugin.java.JavaPlugin

public abstract class RunePlugin: JavaPlugin(), ILifecycle {
    public val core: CorePlugin get() = CorePlugin.instance
    @PublishedApi
    internal val features: MutableMap<RuneFeature<*>, RuneFeatureInstance> = mutableMapOf()

    public open var localeOrNull: PluginLocale? = null
    public var locale: PluginLocale get() = localeOrNull ?: error("Locale not initialized")
        set(value) {
            localeOrNull = value
        }

    final override fun onEnable() {
        onStart()
        for ((_, featureInstance) in features) {
            featureInstance.onStart()
        }
    }

    final override fun onDisable() {
        onShutdown()
        for ((_, featureInstance) in features) {
            featureInstance.onShutdown()
        }
    }

    public fun <T : RuneFeatureInstance> install(feature: RuneFeature<T>, config: T.() -> Unit = {}) {
        val featureInstance = feature.create(this)
        if (!core.globallyInstalledFeatures.contains(feature)) {
            feature.setup(core)
        }
        featureInstance.install()
        featureInstance.apply(config)
        features[feature] = featureInstance
        core.globallyInstalledFeatures.add(feature)
    }

    public inline fun <reified T : RuneFeatureInstance> feature(feature: RuneFeature<T>): T {
        return features[feature] as T
    }

    public fun <T : RuneFeatureInstance> uninstall(feature: RuneFeature<T>) {
        val featureInstance = features[feature] ?: return
        featureInstance.uninstall()
        features.remove(feature)
        core.globallyInstalledFeatures.remove(feature)
    }
}