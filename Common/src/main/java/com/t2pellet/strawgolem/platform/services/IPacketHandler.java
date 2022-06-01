package com.t2pellet.strawgolem.platform.services;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.network.Packet;
import com.t2pellet.strawgolem.network.StrawgolemPackets;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public interface IPacketHandler {


    static void registerIDs() {
        StrawgolemCommon.LOG.info("Registering packet IDs");
        for (Field declaredField : StrawgolemPackets.Client.class.getDeclaredFields()) {
            try {
                StrawgolemPackets.PacketKey<?> id = (StrawgolemPackets.PacketKey<?>) declaredField.get(null);
                Services.PACKETS.registerPacketID(id.getId(), id.getClazz());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (Field declaredField : StrawgolemPackets.Common.class.getDeclaredFields()) {
            try {
                StrawgolemPackets.PacketKey<?> id = (StrawgolemPackets.PacketKey<?>) declaredField.get(null);
                Services.PACKETS.registerPacketID(id.getId(), id.getClazz());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register client packets
     */
    static void registerClient() {
        StrawgolemCommon.LOG.info("Registering client packets");
        for (Field declaredField : StrawgolemPackets.Client.class.getDeclaredFields()) {
            try {
                StrawgolemPackets.PacketKey<?> id = (StrawgolemPackets.PacketKey<?>) declaredField.get(null);
                Services.PACKETS.registerClientPacket(id.getId(), id.getClazz());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register server packets
     */
    static void registerServer() {
        StrawgolemCommon.LOG.info("Registering server packets");
        for (Field declaredField : StrawgolemPackets.Common.class.getDeclaredFields()) {
            try {
                StrawgolemPackets.PacketKey<?> id = (StrawgolemPackets.PacketKey<?>) declaredField.get(null);
                Services.PACKETS.registerServerPacket(id.getId(), id.getClazz());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /* Registry */

    <T extends Packet<T>> void registerPacketID(ResourceLocation id, Class<T> packetClass);

    /**
     * Register server-executing packet
     *
     * @param id          the packet ResourceLocation
     * @param packetClass the packet class
     * @param <T>         the packet type
     */
    <T extends Packet<T>> void registerServerPacket(ResourceLocation id, Class<T> packetClass);

    /**
     * Register client-executing packet
     *
     * @param id          the packet ResourceLocation
     * @param packetClass the packet class
     * @param <T>         the packet type
     */
    <T extends Packet<T>> void registerClientPacket(ResourceLocation id, Class<T> packetClass);

    /* Sending */

    /**
     * Sends packet from client to server
     * @param packet the packet to send
     */
    <T extends Packet<T>> void sendToServer(Packet<T> packet);

    /**
     * Sends packet from server to the given player
     * @param packet the packet to send
     * @param player the player to send to
     */
    <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer player);

    /**
     * Sends packet from server to the given players
     *
     * @param packet  the packet to send
     * @param players the players to send to
     */
    <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer... players);

    /**
     * Sends packet to all players in range of the given entity
     * @param packet the packet to send
     * @param e      the entity in question
     * @param range  the range around the entity
     */
    <T extends Packet<T>> void sendInRange(Packet<T> packet, Entity e, float range);

    /**
     * Sends packet to all players in a given area
     * @param packet the packet to send
     * @param world  the world to send in
     * @param pos    the position to center around
     * @param range  the range around the position
     */
    <T extends Packet<T>> void sendInArea(Packet<T> packet, Level world, BlockPos pos, float range);
}
