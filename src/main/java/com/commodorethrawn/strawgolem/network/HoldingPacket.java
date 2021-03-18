package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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

    HoldingPacket(MinecraftClient client, PacketByteBuf byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void execute() {
        ItemStack stack = ItemStack.fromTag(tag.getCompound("heldStack"));
        World world = net.minecraft.client.MinecraftClient.getInstance().world;
        EntityStrawGolem golem = null;
        if (world != null) golem = (EntityStrawGolem) world.getEntityById(tag.getInt("id"));
        if (golem != null) {
            golem.getInventory().setStack(0, stack);
        }
    }
}
