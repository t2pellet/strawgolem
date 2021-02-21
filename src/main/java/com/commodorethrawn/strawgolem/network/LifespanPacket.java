package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
class LifespanPacket {

    static void execute(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        int[] data = packetByteBuf.readIntArray();
        int lifespan = data[0];
        int id = data[1];
        World world = net.minecraft.client.MinecraftClient.getInstance().world;
        EntityStrawGolem golem = null;
        if (world != null) golem = (EntityStrawGolem) world.getEntityById(id);
        if (golem != null) golem.getLifespan().set(lifespan);
    }
}
