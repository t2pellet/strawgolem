package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PacketHandler {

    public static final Identifier LIFESPAN_PACKET = new Identifier(Strawgolem.MODID, "message_lifespan");
    public static final Identifier HOLDING_PACKET = new Identifier(Strawgolem.MODID, "message_item");

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(LIFESPAN_PACKET, LifespanPacket::execute);
        ClientSidePacketRegistry.INSTANCE.register(HOLDING_PACKET, HoldingPacket::execute);
    }

    public static void sendLifespanPacket(EntityStrawGolem golem) {
        sendPacket(LIFESPAN_PACKET, new int[]{golem.getLifespan().get(), golem.getEntityId()}, golem);
    }

    public static void sendHoldingPacket(EntityStrawGolem golem) {
        ItemStack stack = golem.getMainHandStack();
        int itemID = Item.getRawId(stack.getItem());
        int itemCount = stack.getCount();
        sendPacket(HOLDING_PACKET, new int[]{itemID, itemCount, golem.getEntityId()}, golem);
    }

    public static void sendPacket(Identifier packetID, int[] packetData, EntityStrawGolem golem) {
        List<PlayerEntity> nearbyPlayers = golem.world.getEntitiesByClass(PlayerEntity.class, golem.getBoundingBox().expand(40), e -> true);
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeIntArray(packetData);
        for (PlayerEntity nearbyPlayer : nearbyPlayers) {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(nearbyPlayer, packetID, data);
        }
    }
}
