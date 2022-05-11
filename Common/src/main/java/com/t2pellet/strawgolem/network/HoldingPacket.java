package com.t2pellet.strawgolem.network;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HoldingPacket extends Packet<HoldingPacket> {

    public HoldingPacket(EntityStrawGolem golem) {
        super();
        CompoundTag itemTag = new CompoundTag();
        golem.getMainHandItem().save(itemTag);
        tag.put("heldStack", itemTag);
        tag.putInt("id", golem.getId());
    }

    public HoldingPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    @Override
    public Runnable getExecutor() {
        return new HoldingExecutor();
    }

    class HoldingExecutor implements Runnable {
        @Override
        public void run() {
            ItemStack stack = ItemStack.of(tag.getCompound("heldStack"));
            Level world = net.minecraft.client.Minecraft.getInstance().level;
            EntityStrawGolem golem = null;
            if (world != null) golem = (EntityStrawGolem) world.getEntity(tag.getInt("id"));
            if (golem != null) {
                golem.getInventory().setItem(0, stack);
            }
        }
    }
}
