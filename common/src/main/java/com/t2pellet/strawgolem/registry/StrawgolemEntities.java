package com.t2pellet.strawgolem.registry;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.tlib.registry.api.EntityEntryType;
import com.t2pellet.tlib.registry.api.RegistryClass;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@RegistryClass.IRegistryClass(EntityType.class)
public class StrawgolemEntities implements RegistryClass {

    @IRegistryEntry
    public static final EntityEntryType<StrawGolem> STRAW_GOLEM = new EntityEntryType<>("strawgolem", StrawGolem::new, StrawGolem::createAttributes, MobCategory.CREATURE, 0.9F, 0.6F);

}
