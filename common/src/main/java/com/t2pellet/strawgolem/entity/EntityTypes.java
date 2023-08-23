package com.t2pellet.strawgolem.entity;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.tlib.common.registry.IModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityTypes implements IModEntities {

    public static EntityType<StrawGolem> strawGolem() {
        return STRAW_GOLEM.getType();
    }

    @IEntity(name = Constants.MOD_ID, category = MobCategory.CREATURE, height = 0.9F, width = 0.6F)
    public static final TLibEntity<StrawGolem> STRAW_GOLEM = new TLibEntity<>(StrawGolem::new, StrawGolem.createAttributes());
}
