package com.runerealms.core.ext

import com.runerealms.core.RunePlugin
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface PluginListener: Listener {
    public val plugin: RunePlugin
}

public data class PluginListenerImpl(override val plugin: RunePlugin): PluginListener

@OptIn(ExperimentalContracts::class)
public inline fun RunePlugin.listener(scope: PluginListener.() -> Unit) {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    scope(PluginListenerImpl(this))
}

public inline fun <reified T : Event> PluginListener.handler(
    priority: EventPriority = EventPriority.NORMAL,
    klass: Class<out T> = T::class.java,
    ignoreCancelled: Boolean = false,
    noinline handler: T.() -> Unit
) {
    Bukkit.getPluginManager().registerEvent(
        klass,
        this,
        priority,
        { _, event ->
            if (klass.isInstance(event) && event is T)
                handler(event)
        },
        plugin,
        ignoreCancelled
    )
}

public inline fun <reified T : Event> RunePlugin.eventHandler(
    priority: EventPriority = EventPriority.NORMAL,
    listener: PluginListener = PluginListenerImpl(this),
    klass: Class<out T> = T::class.java,
    ignoreCancelled: Boolean = false,
    noinline handler: T.() -> Unit
): Unit = listener.handler(priority, klass, ignoreCancelled, handler)