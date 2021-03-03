package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public class GreedyPacket extends Packet {

    GreedyPacket(PacketContext ctx, PacketByteBuf byteBuf) {
        super(ctx, byteBuf);
    }

    public GreedyPacket(EntityStrawGolem golem, boolean isGreedy) {
        super();
        tag.putInt("id", golem.getEntityId());
        tag.putBoolean("greedy", isGreedy);
    }

    @Override
    public void execute() {
        World world = MinecraftClient.getInstance().world;
        EntityStrawGolem golem = (EntityStrawGolem) world.getEntityById(tag.getInt("id"));
        RenderStrawGolem renderStrawGolem = (RenderStrawGolem) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(golem);
        renderStrawGolem.getModel().setPlayerHasFood(tag.getBoolean("greedy"));
    }
}
