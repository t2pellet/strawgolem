package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.capability.lifespan.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanProvider;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageLifespan {
    CompoundNBT tag = new CompoundNBT();

    public MessageLifespan(EntityStrawGolem golem) {
        tag.putInt("lifespan", golem.getCurrentLifespan());
        tag.putInt("id", golem.getEntityId());
    }

    //Decoding
    public MessageLifespan(final PacketBuffer buf) {
        tag = buf.readCompoundTag();
    }

    public void encode(final PacketBuffer buf) {
        buf.writeCompoundTag(tag);
    }

    public void onMessage(final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            int time = tag.getInt("lifespan");
            EntityStrawGolem golem = (EntityStrawGolem) Minecraft.getInstance().world.getEntityByID(tag.getInt("id"));
            if (golem != null) {
                ILifespan lifespan = golem.getCapability(LifespanProvider.LIFESPAN_CAP, null)
                        .orElseThrow(() -> new IllegalArgumentException("cant be empty"));
                lifespan.set(time);
            }
        }));
        ctx.get().setPacketHandled(true);
    }
}
