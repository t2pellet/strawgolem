package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

public class StrawgolemPackets {

    private StrawgolemPackets() {
    }

    static class Client {
        static final PacketKey<HealthPacket> LIFESPAN_PACKET = new PacketKey<>(HealthPacket.class, "message_lifespan");
        static final PacketKey<HoldingPacket> HOLDING_PACKET = new PacketKey<>(HoldingPacket.class, "message_item");
        static final PacketKey<GreedyPacket> GREEDY_PACKET = new PacketKey<>(GreedyPacket.class, "message_greedy");
    }

    static class Common {

    }


    static class PacketKey<T extends Packet> {

        private Class<T> clazz;
        private Identifier id;

        private PacketKey(Class<T> clazz, String id) {
            this.clazz = clazz;
            this.id = new Identifier(Strawgolem.MODID, id);
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public Identifier getId() {
            return id;
        }
    }

}
