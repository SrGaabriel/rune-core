package com.runerealms.core.ext

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

public fun JavaPlugin.nextTick(callback: () -> Unit): BukkitTask =
    Bukkit.getScheduler().runTask(this, callback)