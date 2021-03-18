package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.Strawgolem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PacketHandlerImpl implements PacketHandler {

    private final Map<Class<? extends Packet>, Identifier> idMap;

    PacketHandlerImpl() {
        idMap = new HashMap<>();
    }

    @Override
    public <T extends Packet> void registerPacket(Identifier id, Class<T> packetClass) {
        registerPacket(id, packetClass, false);
    }

    @Override
    public <T extends Packet> void registerPacket(Identifier id, Class<T> packetClass, boolean clientOnly) {
        idMap.put(packetClass, id);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(id, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
                try {
                    packetClass.getDeclaredConstructor(MinecraftClient.class, PacketByteBuf.class).newInstance(minecraftClient, packetByteBuf);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    Strawgolem.logger.error("Error: Failed to instantiate packet - " + id);
                }
            });
        } else if (!clientOnly) {
            ServerPlayNetworking.registerGlobalReceiver(id, (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
                try {
                    packetClass.getDeclaredConstructor(MinecraftServer.class, PacketByteBuf.class).newInstance(minecraftServer, packetByteBuf);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    Strawgolem.logger.error("Error: Failed to instantiate packet - " + id);
                }
            });
        }
    }

    @Override
    public void sendToServer(Packet packet) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        packet.encode(data);
        ClientPlayNetworking.send(idMap.get(packet.getClass()), data);
    }

    @Override
    public void sendTo(Packet packet, ServerPlayerEntity player) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        packet.encode(data);
        ServerPlayNetworking.send(player, idMap.get(packet.getClass()), data);
    }

    @Override
    public void sendTo(Packet packet, ServerPlayerEntity... players) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        Identifier id = idMap.get(packet.getClass());
        packet.encode(data);
        for (ServerPlayerEntity player : players) ServerPlayNetworking.send(player, id, data);
    }

    @Override
    public void sendInRange(Packet packet, Entity e, float range) {
        sendInArea(packet, e.getEntityWorld(), e.getBlockPos(), range);
    }

    @Override
    public void sendInArea(Packet packet, World world, BlockPos pos, float range) {
        Box box = new Box(pos);
        List<ServerPlayerEntity> nearbyPlayers = world.getEntitiesByClass(ServerPlayerEntity.class, box.expand(range), p -> true);
        sendTo(packet, nearbyPlayers.toArray(new ServerPlayerEntity[0]));
    }

}
