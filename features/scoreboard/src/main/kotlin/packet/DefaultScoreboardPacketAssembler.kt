package com.runerealms.core.feature.scoreboard.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.AdventureComponentConverter
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedChatComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import java.util.*

public class DefaultScoreboardPacketAssembler(): ScoreboardPacketAssembler {
    // net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay
    private val nmsClass = Class.forName("net.minecraft.world.scores.criteria.IScoreboardCriteria\$EnumScoreboardHealthDisplay")

    override fun createObjective(id: String, text: Component): PacketContainer =
        updateObjective(text, id, 0)

    override fun updateObjective(id: String, text: Component): PacketContainer =
        updateObjective(text, id, 2)

    override fun removeObjective(id: String): PacketContainer =
        updateObjective(Component.empty(), id, 1)

    override fun displayObjective(id: String): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE)
        packet.integers.write(0, 1)
        packet.strings.write(0, id)

        return packet
    }

    private fun updateObjective(text: Component, playerName: String, action: Int): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
        packet.strings.write(0, playerName)
        packet.integers.write(0, action)
        if (action != 1) {
            packet.chatComponents.write(0, AdventureComponentConverter.fromComponent(text))
            packet.getEnumModifier(HealthDisplay::class.java, 2).write(0, HealthDisplay.INTEGER)
        }

        return packet
    }

    override fun createTeam(text: Component): PacketContainer =
        updateTeam(text, 0)

    override fun updateTeam(text: Component): PacketContainer =
        updateTeam(text, 2)

    override fun removeTeam(text: Component): PacketContainer =
        updateTeam(text, 1)

    private fun updateTeam(text: Component, action: Int): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)
        packet.strings.write(0, "ItzGaabriel")
        packet.integers.write(0, action)

        if (action == 1) {
            return packet
        }

        val structure = packet.optionalStructures.readSafely(0).get()

        val teamStructure = Team.toTeam(text)
        structure.chatComponents.write(0, AdventureComponentConverter.fromComponent(teamStructure.player))
        structure.integers.write(0, 0)
        structure.strings.write(0, "always")
        structure.strings.write(1, "always")
        @Suppress("deprecation")
        structure.getEnumModifier(ChatColor::class.java, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, ChatColor.RESET)
        structure.chatComponents.write(1, AdventureComponentConverter.fromComponent(teamStructure.prefix))
        structure.chatComponents.write(2, AdventureComponentConverter.fromComponent(teamStructure.suffix))

        packet.optionalStructures.write(0, Optional.of(structure))
        return packet
    }

    override fun updateScore(objectiveId: String, text: Component, score: Int, action: EnumWrappers.ScoreboardAction): PacketContainer {
        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE)
        val convertedString = LegacyComponentSerializer.legacySection().serialize(text).ifBlank {
            " ".repeat(score)
        }

        packet.strings.write(0, convertedString)
        packet.scoreboardActions.write(0, action)
        packet.strings.write(1, objectiveId)
        packet.integers.write(0, score)
        return packet
    }

    private enum class HealthDisplay {
        INTEGER, HEARTS
    }

    private class Team(
        val prefix: Component,
        val player: Component,
        val suffix: Component
    ) {
        companion object {
            fun toTeam(text: Component): Team {
                val string = text.toString()

                val prefix = if (string.length > 16) string.substring(16, 32) else ""
                val player = string.substring(0, 16)
                val suffix = if (string.length > 48) string.substring(32, 48) else ""

                return Team(
                    Component.text(prefix),
                    Component.text(player),
                    Component.text(suffix)
                )
            }
        }
    }
}