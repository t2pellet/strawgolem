package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.network.Packet;
import com.t2pellet.strawgolem.platform.services.IPacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricPacketHandler implements IPacketHandler {

    private final Map<Class<? extends Packet<?>>, ResourceLocation> idMap;

    public FabricPacketHandler() {
        idMap = new HashMap<>();
    }

    @Override
    public <T extends Packet<T>> void registerPacketID(ResourceLocation id, Class<T> packetClass) {
        idMap.put(packetClass, id);
    }

    @Override
    public <T extends Packet<T>> void registerClientPacket(ResourceLocation id, Class<T> packetClass) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            _registerClientPacket(id, packetClass);
        }
    }

    @Environment(EnvType.CLIENT)
    private <T extends Packet<T>> void _registerClientPacket(ResourceLocation id, Class<T> packetClass) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
            try {
                T packet = packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(buf);
                Services.SIDE.scheduleClient(packet.getExecutor());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ex) {
                StrawgolemCommon.LOG.error("Error: Failed to instantiate packet - " + id);
            }
        });
    }

    @Override
    public <T extends Packet<T>> void registerServerPacket(ResourceLocation id, Class<T> packetClass) {
        ServerPlayNetworking.registerGlobalReceiver(id, (minecraftServer, serverPlayer, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            try {
                T packet = packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(packetByteBuf);
                Services.SIDE.scheduleServer(packet.getExecutor());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ex) {
                StrawgolemCommon.LOG.error("Error: Failed to instantiate packet - " + id);
            }
        });
    }

    @Override
    public <T extends Packet<T>> void sendToServer(Packet<T> packet) {
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(data);
        ClientPlayNetworking.send(idMap.get(packet.getClass()), data);
    }

    @Override
    public <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer player) {
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(data);
        ServerPlayNetworking.send(player, idMap.get(packet.getClass()), data);
    }

    @Override
    public <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer... players) {
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        ResourceLocation id = idMap.get(packet.getClass());
        packet.encode(data);
        for (ServerPlayer player : players) ServerPlayNetworking.send(player, id, data);
    }

    @Override
    public <T extends Packet<T>> void sendInRange(Packet<T> packet, Entity e, float range) {
        sendInArea(packet, e.level, e.blockPosition(), range);
    }

    @Override
    public <T extends Packet<T>> void sendInArea(Packet<T> packet, Level world, BlockPos pos, float range) {
        AABB box = new AABB(pos);
        List<ServerPlayer> nearbyPlayers = world.getEntitiesOfClass(ServerPlayer.class, box.inflate(range), p -> true);
        sendTo(packet, nearbyPlayers.toArray(new ServerPlayer[0]));
    }

}
