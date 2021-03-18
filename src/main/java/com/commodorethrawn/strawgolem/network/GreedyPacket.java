package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public class GreedyPacket extends Packet {

    public GreedyPacket(EntityStrawGolem golem, boolean isGreedy) {
        super();
        tag.putInt("id", golem.getEntityId());
        tag.putBoolean("greedy", isGreedy);
    }

    GreedyPacket(MinecraftClient client, PacketByteBuf byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void execute() {
        World world = net.minecraft.client.MinecraftClient.getInstance().world;
        if (world != null) {
            EntityStrawGolem golem = (EntityStrawGolem) world.getEntityById(tag.getInt("id"));
            if (golem != null) golem.setTempted(tag.getBoolean("greedy"));
        }
    }
}
