package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.client.renderer.entity.RenderStrawGolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

class HoldingPacket {

    static void execute(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        int[] data = packetByteBuf.readIntArray();
        int itemID = data[0];
        int itemCount = data[1];
        int id = data[2];
        World world = net.minecraft.client.MinecraftClient.getInstance().world;
        EntityStrawGolem golem = null;
        if (world != null) golem = (EntityStrawGolem) world.getEntityById(id);
        if (golem != null) {
            ItemStack stack = new ItemStack(Item.byRawId(itemID), itemCount);
            golem.getInventory().setStack(0, stack);
        }
    }

}
