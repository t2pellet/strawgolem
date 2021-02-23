package com.commodorethrawn.strawgolem.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;

@Mixin(MobEntity.class)
public interface TargetSelectorAccessor {
    @Accessor("targetSelector")
    public GoalSelector targetSelector();

}
