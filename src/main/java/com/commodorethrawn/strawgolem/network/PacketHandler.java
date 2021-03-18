package com.commodorethrawn.strawgolem.network;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;

import static com.commodorethrawn.strawgolem.network.StrawgolemPackets.*;

public interface PacketHandler {
    PacketHandler INSTANCE = new PacketHandlerImpl();

    /**
     * Register all packets
     */
    static void register() {
        registerClient();
        registerServer();
    }

    /**
     * Register client packets
     */
    static void registerClient() {
        for (Field declaredField : StrawgolemPackets.Client.class.getDeclaredFields()) {
            try {
                PacketKey<?> id = (PacketKey<?>) declaredField.get(null);
                INSTANCE.registerPacket(id.getId(), id.getClazz());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register server/common packets
     */
    static void registerServer() {
        for (Field declaredField : StrawgolemPackets.Common.class.getDeclaredFields()) {
            try {
                PacketKey<?> id = (PacketKey<?>) declaredField.get(null);
                INSTANCE.registerPacket(id.getId(), id.getClazz());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /* Registry */

    /**
     * Register given packet on server and client
     * @param id the packet identifier
     * @param packetClass the packet class
     * @param <T> the packet type
     */
    <T extends Packet> void registerPacket(Identifier id, Class<T> packetClass);

    /**
     * Register given packet on client and potentially server
     * @param id the packet identifier
     * @param packetClass the packet class
     * @param clientOnly whether this packet is client only (should not be registered on server)
     * @param <T> the packet type
     */
    <T extends Packet> void registerPacket(Identifier id, Class<T> packetClass, boolean clientOnly);

    /* Sending */

    /**
     * Sends packet from client to server
     * @param packet the packet to send
     */
    void sendToServer(Packet packet);

    /**
     * Sends packet from server to the given player
     * @param packet the packet to send
     * @param player the player to send to
     */
    void sendTo(Packet packet, ServerPlayerEntity player);

    /**
     * Sends packet from server to the given players
     * @param packet the packet to send
     * @param players the players to send to
     */
    void sendTo(Packet packet, ServerPlayerEntity... players);

    /**
     * Sends packet to all players in range of the given entity
     * @param packet the packet to send
     * @param e the entity in question
     * @param range the range around the entity
     */
    void sendInRange(Packet packet, Entity e, float range);

    /**
     * Sends packet to all players in a given area
     * @param packet the packet to send
     * @param world the world to send in
     * @param pos the position to center around
     * @param range the range around the position
     */
    void sendInArea(Packet packet, World world, BlockPos pos, float range);
}
