package com.t2pellet.strawgolem.network;

import com.t2pellet.strawgolem.StrawgolemCommon;
import net.minecraft.resources.ResourceLocation;

public class StrawgolemPackets {

    private StrawgolemPackets() {
    }

    public static class Client {
        public static final PacketKey<HealthPacket> LIFESPAN_PACKET = new PacketKey<>(HealthPacket.class, "message_lifespan");
        public static final PacketKey<HoldingPacket> HOLDING_PACKET = new PacketKey<>(HoldingPacket.class, "message_item");
        public static final PacketKey<GreedyPacket> GREEDY_PACKET = new PacketKey<>(GreedyPacket.class, "message_greedy");
    }

    public static class Common {
    }


    public static class PacketKey<T extends Packet> {

        private Class<T> clazz;
        private ResourceLocation id;

        private PacketKey(Class<T> clazz, String id) {
            this.clazz = clazz;
            this.id = new ResourceLocation(StrawgolemCommon.MODID, id);
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public ResourceLocation getId() {
            return id;
        }
    }

}
