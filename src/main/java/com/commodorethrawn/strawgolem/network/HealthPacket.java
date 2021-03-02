package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.util.scheduler.ClientScheduler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class HealthPacket extends Packet {

    public HealthPacket(EntityStrawGolem golem) {
        super();
        tag.putInt("lifespan", golem.getLifespan().get());
        tag.putInt("hunger", golem.getHunger().get());
        tag.putInt("id", golem.getEntityId());
    }

    HealthPacket(PacketContext ctx, PacketByteBuf byteBuf) {
        super(ctx, byteBuf);
    }

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
