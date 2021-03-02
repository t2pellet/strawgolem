package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.util.scheduler.ClientScheduler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public class HoldingPacket extends Packet {

    public HoldingPacket(EntityStrawGolem golem) {
        super();
        tag.put("heldStack", golem.getMainHandStack().toTag(new CompoundTag()));
        tag.putInt("id", golem.getEntityId());
    }

    HoldingPacket(PacketContext ctx, PacketByteBuf byteBuf) {
        super(ctx, byteBuf);
    }

    @Override
    public void execute() {
        System.out.println("Holding packet!");
        ItemStack stack = ItemStack.fromTag(tag.getCompound("heldStack"));
        World world = net.minecraft.client.MinecraftClient.getInstance().world;
        EntityStrawGolem golem = null;
        if (world != null) golem = (EntityStrawGolem) world.getEntityById(tag.getInt("id"));
        if (golem != null) {
            System.out.println("Holding packet good!");
            golem.getInventory().setStack(0, stack);
        }
    }
}
