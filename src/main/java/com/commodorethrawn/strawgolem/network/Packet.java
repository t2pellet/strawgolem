package com.commodorethrawn.strawgolem.network;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public abstract class Packet {

    CompoundTag tag;

    Packet(PacketContext ctx, PacketByteBuf byteBuf) {
        this.tag = byteBuf.readCompoundTag();
        ctx.getTaskQueue().execute(this::execute);
    }

    public Packet() {
        tag = new CompoundTag();
    }

    public void encode(PacketByteBuf byteBuf) {
        byteBuf.writeCompoundTag(tag);
    }

    public abstract void execute();


}
