package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public class HealthPacket extends Packet {

    public HealthPacket(EntityStrawGolem golem) {
        super();
        tag.putInt("lifespan", golem.getLifespan().get());
        tag.putInt("hunger", golem.getHunger().get());
        tag.putInt("id", golem.getEntityId());
    }

    HealthPacket(MinecraftClient client, PacketByteBuf byteBuf) {
        super(client, byteBuf);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void execute() {
        int lifespan = tag.getInt("lifespan");
        int hunger = tag.getInt("hunger");
        int id = tag.getInt("id");
        World world = net.minecraft.client.MinecraftClient.getInstance().world;
        if (world != null) {
            EntityStrawGolem golem = (EntityStrawGolem) world.getEntityById(id);
            if (golem != null) {
                golem.getLifespan().set(lifespan);
                golem.getHunger().set(hunger);
            }
        }
    }
}
