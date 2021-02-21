package com.commodorethrawn.strawgolem.mixin;

import com.commodorethrawn.strawgolem.entity.ai.PickupGolemGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class IronGolemMixin {

    @Inject(method = "addEntity", at = @At("TAIL"))
    public void patchIronGolem1(Entity entity, CallbackInfoReturnable<?> info) {
        patchIronGolem(entity);
    }

    @Inject(method = "loadEntityUnchecked", at = @At("TAIL"))
    public void patchIronGolem2(Entity entity, CallbackInfo info) {
        patchIronGolem(entity);
    }

    public void patchIronGolem(Entity entity) {
        if (entity instanceof IronGolemEntity) {
            IronGolemEntity ironGolem = (IronGolemEntity) entity;
            ((GoalSelectorAccessor) ironGolem).goalSelector().add(2, new PickupGolemGoal(ironGolem, 0.8D));
        }
    }


}
