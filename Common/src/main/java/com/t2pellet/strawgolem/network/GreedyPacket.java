package com.t2pellet.strawgolem.network;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class GreedyPacket extends Packet<GreedyPacket> {

    public GreedyPacket(EntityStrawGolem golem, boolean isGreedy) {
        super();
        tag.putInt("id", golem.getId());
        tag.putBoolean("greedy", isGreedy);
    }

    public GreedyPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public Runnable getExecutor() {
        return new GreedyExecutor();
    }

    class GreedyExecutor implements Runnable {
        @Override
        public void run() {
            Level world = net.minecraft.client.Minecraft.getInstance().level;
            if (world != null) {
                EntityStrawGolem golem = (EntityStrawGolem) world.getEntity(tag.getInt("id"));
                if (golem != null) golem.setTempted(tag.getBoolean("greedy"));
            }
        }
    }
}
