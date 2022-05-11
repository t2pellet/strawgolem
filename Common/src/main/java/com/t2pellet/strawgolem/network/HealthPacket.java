package com.t2pellet.strawgolem.network;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;

public class HealthPacket extends Packet<HealthPacket> {

    public HealthPacket(EntityStrawGolem golem) {
        super();
        tag.putInt("lifespan", golem.getLifespan().get());
        tag.putInt("hunger", golem.getHunger().get());
        tag.putInt("id", golem.getId());
    }

    public HealthPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public Runnable getExecutor() {
        return new HealthExecutor();
    }

    class HealthExecutor implements Runnable {

        @Override
        public void run() {
            int lifespan = tag.getInt("lifespan");
            int hunger = tag.getInt("hunger");
            int id = tag.getInt("id");
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                EntityStrawGolem golem = (EntityStrawGolem) world.getEntity(id);
                if (golem != null) {
                    golem.getLifespan().set(lifespan);
                    golem.getHunger().set(hunger);
                }
            }
        }
    }
}
