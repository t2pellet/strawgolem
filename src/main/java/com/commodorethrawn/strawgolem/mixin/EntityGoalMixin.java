package com.commodorethrawn.strawgolem.mixin;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.ai.MunchGolemGoal;
import com.commodorethrawn.strawgolem.entity.ai.PickupGolemGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class EntityGoalMixin {

    @Inject(method = "addEntity", at = @At("TAIL"))
    public void patchNewEntity(Entity entity, CallbackInfoReturnable<?> info) {
        patchIronGolem(entity);
        patchRaider(entity);
        patchAnimal(entity);
    }

    @Inject(method = "loadEntityUnchecked", at = @At("TAIL"))
    public void patchOldEntity(Entity entity, CallbackInfo info) {
        patchIronGolem(entity);
        patchRaider(entity);
        patchAnimal(entity);
    }

    public void patchIronGolem(Entity entity) {
        if (entity instanceof IronGolemEntity) {
            IronGolemEntity ironGolem = (IronGolemEntity) entity;
            ((GoalSelectorAccessor) ironGolem).goalSelector().add(2, new PickupGolemGoal(ironGolem, 0.8D));
        }
    }

    public void patchRaider(Entity entity) {
        if (entity instanceof RaiderEntity) {
            RaiderEntity raider = (RaiderEntity) entity;
            ((TargetSelectorAccessor) raider).targetSelector().add(2, new FollowTargetGoal<>(raider, EntityStrawGolem.class, true));
        }
    }

    public void patchAnimal(Entity entity) {
        if (entity instanceof AnimalEntity) {
            AnimalEntity animal = (AnimalEntity) entity;
            ((GoalSelectorAccessor) animal).goalSelector().add(2, new MunchGolemGoal(animal, 0.8D));
        }
    }


}
