package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public class RaiderMixin extends Mob {

    protected RaiderMixin(EntityType<? extends Mob> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void registerGoals(CallbackInfo ci) {
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>((Raider) (Object) this, StrawGolem.class, true));
    }


}
