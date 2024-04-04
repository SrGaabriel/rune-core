package com.runerealms.core.feature.scoreboard

import com.comphenix.protocol.ProtocolLibrary
import com.runerealms.core.CorePlugin
import com.runerealms.core.RunePlugin
import com.runerealms.core.ext.inTicks
import com.runerealms.core.ext.inWholeTicksInt
import com.runerealms.core.feature.RuneFeature
import com.runerealms.core.feature.RuneFeatureInstance
import com.runerealms.core.feature.scoreboard.packet.DefaultScoreboardPacketAssembler
import com.runerealms.core.feature.scoreboard.packet.ScoreboardPacketAssembler
import com.runerealms.core.feature.scoreboard.struct.Scoreboard
import org.bukkit.Bukkit
import java.util.UUID
import kotlin.math.floor

public class Scoreboards(plugin: RunePlugin): RuneFeatureInstance(plugin) {
    internal val protocolManager get() = ProtocolLibrary.getProtocolManager()
    public var packetAssembler: ScoreboardPacketAssembler = DefaultScoreboardPacketAssembler()

    public fun register(scoreboard: Scoreboard) {
        registerScoreboard(scoreboard)
    }

    public fun unregister(scoreboard: Scoreboard) {
        deleteScoreboard(scoreboard)
    }

    internal fun registerViewers(scoreboard: Scoreboard, viewers: Set<UUID>) {
        val viewersMap: MutableMap<UUID, Scoreboard> = CorePlugin.instance.getGlobalFeatureDataset("scoreboards.viewers") ?: error("Scoreboards viewers not found")
        viewers.forEach { viewersMap[it] = scoreboard }
    }

    internal fun getViewers(scoreboard: Scoreboard): Set<UUID> {
        val viewersMap: MutableMap<UUID, Scoreboard> = CorePlugin.instance.getGlobalFeatureDataset("scoreboards.viewers") ?: error("Scoreboards viewers not found")
        return viewersMap.filter { it.value == scoreboard }.keys
    }

    internal fun unregisterViewers(viewers: Set<UUID>) {
        val viewersMap: MutableMap<UUID, Scoreboard> = CorePlugin.instance.getGlobalFeatureDataset("scoreboards.viewers") ?: error("Scoreboards viewers not found")
        viewers.forEach { viewersMap.remove(it) }
    }

    public companion object: RuneFeature<Scoreboards>() {
        public override fun setup(core: CorePlugin) {
            createGlobalDataset("scoreboards.list", mutableListOf<Scoreboard>())
            createGlobalDataset("scoreboards.viewers", mutableMapOf<UUID, Scoreboard>())

            var taskLifespan = 0
            Bukkit.getScheduler().runTaskTimerAsynchronously(core, Runnable {
                val scoreboards: MutableList<Scoreboard> = core.getGlobalFeatureDataset("scoreboards.list") ?: error("Scoreboards list not found")
                scoreboards.forEach { scoreboard ->
                    if (!scoreboard.valid) return@forEach

                    if (taskLifespan % scoreboard.globalUpdateInterval.inWholeTicksInt == 0) {
                        scoreboard.update(floor(taskLifespan / scoreboard.globalUpdateInterval.inTicks).toInt())
                        return@forEach
                    }

                    if (scoreboard.title!!.customUpdateInterval != null && taskLifespan % scoreboard.title!!.customUpdateInterval!!.inWholeTicksInt == 0) {
                        scoreboard.updateTitle(floor(taskLifespan / scoreboard.title!!.customUpdateInterval!!.inTicks).toInt())
                    } else {
                        for (line in scoreboard.lines) {
                            if (line.customUpdateInterval != null && taskLifespan % line.customUpdateInterval!!.inWholeTicksInt == 0) {
                                scoreboard.updateLine(line, floor(taskLifespan / line.customUpdateInterval!!.inTicks).toInt())
                            }
                        }
                    }
                }
                taskLifespan++
            }, 100, 1)
        }

        public fun registerScoreboard(scoreboard: Scoreboard) {
            val scoreboards: MutableList<Scoreboard> = CorePlugin.instance.getGlobalFeatureDataset("scoreboards.list") ?: error("Scoreboards list not found")
            scoreboards.add(scoreboard)
        }

        public fun deleteScoreboard(scoreboard: Scoreboard) {
            val scoreboards: MutableList<Scoreboard> = CorePlugin.instance.getGlobalFeatureDataset("scoreboards.list") ?: error("Scoreboards list not found")
            scoreboards.remove(scoreboard)
        }

        public override fun create(plugin: RunePlugin): Scoreboards =
            Scoreboards(plugin)
    }
}
