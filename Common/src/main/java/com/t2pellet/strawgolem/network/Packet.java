package com.t2pellet.strawgolem.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public abstract class Packet<T extends Packet<T>> {

    CompoundTag tag;

    public Packet(FriendlyByteBuf byteBuf) {
        this.tag = byteBuf.readNbt();
    }

    public Packet() {
        tag = new CompoundTag();
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(tag);
    }

    public abstract Runnable getExecutor();


}
