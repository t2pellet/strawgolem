package com.t2pellet.strawgolem.registry;

import com.t2pellet.tlib.registry.api.ItemEntryType;
import com.t2pellet.tlib.registry.api.RegistryClass;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

@RegistryClass.IRegistryClass(Item.class)
public class StrawgolemItems implements RegistryClass {

    private static final Item.Properties strawHatProperties = new Item.Properties()
            .stacksTo(1)
            .tab(CreativeModeTab.TAB_DECORATIONS);

    @IRegistryEntry
    public static final ItemEntryType strawHat = new ItemEntryType("straw_hat", strawHatProperties);
}
