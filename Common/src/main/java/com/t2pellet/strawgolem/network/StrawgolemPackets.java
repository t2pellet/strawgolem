package com.t2pellet.strawgolem.network;

import com.t2pellet.strawgolem.StrawgolemCommon;
import net.minecraft.resources.ResourceLocation;

public class StrawgolemPackets {

    private StrawgolemPackets() {
    }

    public static class Client {
        public static final PacketKey<CapabilityPacket> CAPABILITY_PACKET = new PacketKey<>(CapabilityPacket.class, "message_capabilities");
        public static final PacketKey<HoldingPacket> HOLDING_PACKET = new PacketKey<>(HoldingPacket.class, "message_item");
        public static final PacketKey<GreedyPacket> GREEDY_PACKET = new PacketKey<>(GreedyPacket.class, "message_greedy");
    }

    public static class Server {
    }

    public static class PacketKey<T extends Packet<T>> {

        private final Class<T> clazz;
        private final ResourceLocation id;

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
