package com.runerealms.core.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

public class PacketBuilder(private val type: PacketType) {
    private val container = PacketContainer(type)


}

public fun Player.sendPacket(packet: Packet<*>) {
    (this as CraftPlayer).sendPacket(packet)
}