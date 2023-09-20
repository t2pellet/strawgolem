package com.t2pellet.strawgolem.mixin;


import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.goals.MonsterAttackGolemGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public class MonsterMixin extends Mob {

    protected MonsterMixin(EntityType<? extends Mob> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        if (StrawgolemConfig.Behaviour.raidersAttackGolems.get()) {
            targetSelector.addGoal(3, new MonsterAttackGolemGoal(this));
        }
    }
}
