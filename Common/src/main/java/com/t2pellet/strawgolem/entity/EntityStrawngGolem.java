package com.t2pellet.strawgolem.entity;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.ai.GolemTetherGoal;
import com.t2pellet.strawgolem.entity.ai.PickupGolemGoal;
import com.t2pellet.strawgolem.entity.ai.TrackStrawngGolemTargetGoal;
import com.t2pellet.strawgolem.entity.capability.CapabilityHandler;
import com.t2pellet.strawgolem.entity.capability.tether.IHasTether;
import com.t2pellet.strawgolem.entity.capability.tether.Tether;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityStrawngGolem extends AbstractGolem implements IHasTether {

    private static final ResourceLocation LOOT = new ResourceLocation(StrawgolemCommon.MODID, "strawnggolem");

    private int attackAnimationTick;

    public static AttributeSupplier.Builder createMob() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ATTACK_SPEED, 0.1D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D);
    }

    private final Tether tether;

    public EntityStrawngGolem(EntityType<? extends EntityStrawngGolem> entityType, Level world) {
        super(entityType, world);
        maxUpStep = 1.0F;
        tether = CapabilityHandler.INSTANCE.get(Tether.class).orElseThrow(() -> new InstantiationError("Failed to create tether cap"));
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LOOT;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        if (StrawgolemConfig.Tether.isTetherEnabled()) {
            goalSelector.addGoal(2, new GolemTetherGoal<>(this, 0.8D));
        }
        goalSelector.addGoal(3, new PickupGolemGoal(this, 0.6D));
        goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 0.7D, StrawgolemConfig.Tether.getTetherMaxRange()));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new TrackStrawngGolemTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, e -> e instanceof Enemy && !(e instanceof Creeper)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);
        return super.doHurtTarget(target);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 4) {
            this.attackAnimationTick = 10;
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (player.getItemInHand(hand).getItem() == Items.WHEAT && !level.isClientSide()) {
            setHealth(getHealth() + 0.5F);
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            // Feedback
            playSound(SoundEvents.GRASS_STEP, 0.5F, 1.0F);
            spawnHealParticles(getX(), getY(), getZ());
            // Result
            return InteractionResult.CONSUME;
        }
        return super.interactAt(player, vec, hand);
    }

    private void spawnHealParticles(double x, double y, double z) {
        level.addParticle(
                ParticleTypes.HEART,
                x + random.nextDouble() - 0.5, y + 0.4D, z + random.nextDouble() - 0.5,
                this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    }

    @Override
    public Tether getTether() {
        return tether;
    }

    public int getAttackTicks() {
        return this.attackAnimationTick;
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    public void load(CompoundTag nbt) {
        if (nbt.contains("tether")) tether.readTag(nbt.get("tether"));
        super.load(nbt);
    }

    @Override
    public boolean save(CompoundTag nbt) {
        nbt.put("tether", tether.writeTag());
        return super.save(nbt);
    }

}
