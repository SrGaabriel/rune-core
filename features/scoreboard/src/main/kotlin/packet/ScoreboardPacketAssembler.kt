package com.runerealms.core.feature.scoreboard.packet

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction
import net.kyori.adventure.text.Component

public interface ScoreboardPacketAssembler {
    public fun createObjective(id: String, text: Component): PacketContainer

    public fun updateObjective(id: String, text: Component): PacketContainer

    public fun removeObjective(id: String): PacketContainer

    public fun displayObjective(id: String): PacketContainer

    public fun createTeam(text: Component): PacketContainer

    public fun updateTeam(text: Component): PacketContainer

    public fun removeTeam(text: Component): PacketContainer

    public fun updateScore(objectiveId: String, text: Component, score: Int, action: ScoreboardAction): PacketContainer
}