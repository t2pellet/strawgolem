package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.Strawgolem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketHandler {

    private static final Identifier LIFESPAN_PACKET = new Identifier(Strawgolem.MODID, "message_lifespan");
    private static final Identifier HOLDING_PACKET = new Identifier(Strawgolem.MODID, "message_item");
    private static final Identifier GREEDY_PACKET = new Identifier(Strawgolem.MODID, "message_greedy");

    public static final PacketHandler INSTANCE = new PacketHandler();

    private final Map<Class<? extends Packet>, Identifier> idMap;

    public static void register() {
        INSTANCE.registerPacket(LIFESPAN_PACKET, HealthPacket.class);
        INSTANCE.registerPacket(HOLDING_PACKET, HoldingPacket.class);
        INSTANCE.registerPacket(GREEDY_PACKET, GreedyPacket.class);

    }

    private PacketHandler() {
        idMap = new HashMap<>();
    }

    public <T extends Packet> void registerPacket(Identifier id, Class<T> packetClass) {
        idMap.put(packetClass, id);
        ClientSidePacketRegistry.INSTANCE.register(id, (ctx, byteBuf) -> {
            try {
                packetClass.getDeclaredConstructor(PacketContext.class, PacketByteBuf.class).newInstance(ctx, byteBuf);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                Strawgolem.logger.warn("Warning: Failed to instantiate packet - " + id);
            }
        });
    }

    public void sendTo(Packet packet, PlayerEntity player) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        packet.encode(data);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, idMap.get(packet.getClass()), data);
    }

    public void sendInRange(Packet packet, Entity e, float range) {
        sendInArea(packet, e.getEntityWorld(), e.getBlockPos(), range);
    }

    public void sendInArea(Packet packet, World world, BlockPos pos, float range) {
        Box box = new Box(pos);
        List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, box.expand(range), p -> true);
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        packet.encode(data);
        for (PlayerEntity nearbyPlayer : nearbyPlayers) {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(nearbyPlayer, idMap.get(packet.getClass()), data);
        }
    }

}
