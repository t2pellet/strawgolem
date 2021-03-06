package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.ai.PickupGolemGoal;
import com.commodorethrawn.strawgolem.entity.ai.TetherGoal;
import com.commodorethrawn.strawgolem.entity.ai.TrackStrawngGolemTargetGoal;
import com.commodorethrawn.strawgolem.entity.capability.CapabilityHandler;
import com.commodorethrawn.strawgolem.entity.capability.tether.IHasTether;
import com.commodorethrawn.strawgolem.entity.capability.tether.Tether;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EntityStrawngGolem extends GolemEntity implements IHasTether {

    private static final Identifier LOOT = new Identifier(Strawgolem.MODID, "strawnggolem");
    private static final TrackedData<Integer> ATTACK_TICKS = TrackedDataHandlerRegistry.INTEGER.create(18);

    public static DefaultAttributeContainer.Builder createMob() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.1D)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D);
    }

    private final Tether tether;

    public EntityStrawngGolem(EntityType<? extends EntityStrawngGolem> entityType, World world) {
        super(entityType, world);
        tether = CapabilityHandler.INSTANCE.get(Tether.class).orElseThrow(() -> new InstantiationError("Failed to create tether cap"));
        dataTracker.startTracking(ATTACK_TICKS, 0);
    }

    @Override
    protected Identifier getLootTableId() {
        return LOOT;
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        if (ConfigHelper.isTetherEnabled()) {
            goalSelector.add(2, new TetherGoal<>(this, 0.85D));
        }
        goalSelector.add(3, new PickupGolemGoal(this, 0.6D));
        goalSelector.add(4, new WanderNearTargetGoal(this, 0.7D, ConfigHelper.getTetherMaxRange()));
        goalSelector.add(5, new WanderAroundGoal(this, 0.6D));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        goalSelector.add(7, new LookAroundGoal(this));
        targetSelector.add(1, new TrackStrawngGolemTargetGoal(this));
        targetSelector.add(2, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, e -> {
            return e instanceof Monster && !(e instanceof CreeperEntity);
        }));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        int attackTicks = dataTracker.get(ATTACK_TICKS);
        if (attackTicks > 0) {
            dataTracker.set(ATTACK_TICKS, attackTicks - 1);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        dataTracker.set(ATTACK_TICKS, 5);
        return super.tryAttack(target);
    }

    @Override
    public Tether getTether() {
        return tether;
    }

    public int getAttackTicks() {
        return dataTracker.get(ATTACK_TICKS);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("tether", tether.writeTag());
        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("tether")) tether.readTag(tag.get("tether"));
        super.fromTag(tag);
    }
}
