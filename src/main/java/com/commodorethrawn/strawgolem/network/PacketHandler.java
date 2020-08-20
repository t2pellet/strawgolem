package com.commodorethrawn.strawgolem.network;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Strawgolem.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private PacketHandler() {
    }

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id, MessageLifespan.class, MessageLifespan::encode, MessageLifespan::new, MessageLifespan::onMessage);
    }
}
