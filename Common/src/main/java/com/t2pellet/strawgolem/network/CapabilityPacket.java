package com.t2pellet.strawgolem.network;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;

public class CapabilityPacket extends Packet<CapabilityPacket> {

    public CapabilityPacket(StrawGolem golem) {
        super();
        tag.putInt("golemId", golem.getId());
        tag.put("capabilities", golem.getCapabilityManager().writeTag());
    }

    public CapabilityPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public Runnable getExecutor() {
        return new CapabilityExecutor();
    }

    class CapabilityExecutor implements Runnable {
        @Override
        public void run() {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                StrawGolem golem = (StrawGolem) world.getEntity(tag.getInt("golemId"));
                if (golem != null) {
                    golem.getCapabilityManager().readTag(tag.get("capabilities"));
                }
            }
        }
    }
}
