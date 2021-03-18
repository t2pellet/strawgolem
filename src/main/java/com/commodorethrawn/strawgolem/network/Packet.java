package com.commodorethrawn.strawgolem.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public abstract class Packet {

    CompoundTag tag;

    Packet(MinecraftClient client, PacketByteBuf byteBuf) {
        this.tag = byteBuf.readCompoundTag();
        client.execute(this::execute);
    }

    Packet(MinecraftServer server, PacketByteBuf byteBuf) {
        this.tag = byteBuf.readCompoundTag();
        server.execute(this::execute);
    }

    public Packet() {
        tag = new CompoundTag();
    }

    public void encode(PacketByteBuf byteBuf) {
        byteBuf.writeCompoundTag(tag);
    }

    public abstract void execute();


}
