package com.t2pellet.strawgolem.platform;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.network.Packet;
import com.t2pellet.strawgolem.platform.services.IPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgePacketHandler implements IPacketHandler {

    private static final String PROTOCOL_VERSION = "4";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(StrawgolemCommon.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private final Map<ResourceLocation, Integer> idMap = new HashMap<>();

    @Override
    public <T extends Packet<T>> void registerPacketID(ResourceLocation id, Class<T> packetClass) {
        idMap.put(id, idMap.size());
    }

    @Override
    public <T extends Packet<T>> void registerServerPacket(ResourceLocation id, Class<T> packetClass) {
        registerPacket(id, packetClass);
    }

    @Override
    public <T extends Packet<T>> void registerClientPacket(ResourceLocation id, Class<T> packetClass) {
        registerPacket(id, packetClass);
    }

    private <T extends Packet<T>> void registerPacket(ResourceLocation id, Class<T> packetClass) {
        INSTANCE.registerMessage(idMap.get(id), packetClass, Packet::encode, friendlyByteBuf -> {
            try {
                return packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(friendlyByteBuf);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ex) {
                StrawgolemCommon.LOG.error("Error: Failed to instantiate packet - " + id);
            }
            return null;
        }, (t, contextSupplier) -> {
            if (contextSupplier.get().getDirection().getReceptionSide().isClient()) {
                Services.SIDE.scheduleClient(t.getExecutor());
            } else {
                Services.SIDE.scheduleServer(t.getExecutor());
            }
            contextSupplier.get().setPacketHandled(true);
        });
    }

    @Override
    public <T extends Packet<T>> void sendToServer(Packet<T> packet) {
        INSTANCE.sendToServer(packet);
    }

    @Override
    public <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer... players) {
        for (ServerPlayer player : players) {
            sendTo(packet, player);
        }
    }

    @Override
    public <T extends Packet<T>> void sendInRange(Packet<T> packet, Entity e, float range) {
        sendInArea(packet, e.getLevel(), e.blockPosition(), range);
    }

    @Override
    public <T extends Packet<T>> void sendInArea(Packet<T> packet, Level world, BlockPos pos, float range) {
        AABB box = new AABB(pos);
        List<ServerPlayer> nearbyPlayers = world.getEntitiesOfClass(ServerPlayer.class, box.inflate(range), p -> true);
        sendTo(packet, nearbyPlayers.toArray(new ServerPlayer[0]));
    }
}
