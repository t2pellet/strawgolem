package com.t2pellet.strawgolem.entity;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.animations.StrawgolemArmsController;
import com.t2pellet.strawgolem.entity.animations.StrawgolemHarvestController;
import com.t2pellet.strawgolem.entity.animations.StrawgolemMovementController;
import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.entity.capabilities.harvester.Harvester;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import com.t2pellet.strawgolem.entity.capabilities.tether.Tether;
import com.t2pellet.strawgolem.entity.goals.golem.*;
import com.t2pellet.strawgolem.registry.StrawgolemItems;
import com.t2pellet.strawgolem.registry.StrawgolemParticles;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.tlib.Services;
import com.t2pellet.tlib.entity.capability.api.CapabilityManager;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

// TODO : Wood armor (for straw golem to protect from cow / sheep and other things)
// TODO : Fix bug - Animation transition on world load
// TODO : Fix bug - not always walking fully to destination
public class StrawGolem extends AbstractGolem implements IAnimatable, ICapabilityHaver {

    public static final Item REPAIR_ITEM = Registry.ITEM.get(new ResourceLocation(StrawgolemConfig.Lifespan.repairItem.get()));
    private static final double WALK_DISTANCE = 0.00000001D;
    private static final double RUN_DISTANCE = 0.003D;

    // Synched Data
    private static final EntityDataAccessor<Boolean> IS_SCARED = SynchedEntityData.defineId(StrawGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_HAT = SynchedEntityData.defineId(StrawGolem.class, EntityDataSerializers.BOOLEAN);

    // Capabilities
    CapabilityManager capabilities = CapabilityManager.newInstance(this);
    private final Decay decay;
    private final HeldItem heldItem;
    private final Harvester harvester;
    private final Deliverer deliverer;
    private final Tether tether;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.MAX_HEALTH, StrawgolemConfig.Lifespan.baseHealth.get());
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public StrawGolem(EntityType<? extends StrawGolem> type, Level level) {
        super(type, level);
        decay = capabilities.addCapability(Decay.class);
        heldItem = capabilities.addCapability(HeldItem.class);
        harvester = capabilities.addCapability(Harvester.class);
        deliverer = capabilities.addCapability(Deliverer.class);
        tether = capabilities.addCapability(Tether.class);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SCARED, false);
        this.entityData.define(HAS_HAT, false);
    }

    /* AI */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Monster.class, 8.0F, 0.5D, 0.7D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Evoker.class, 12.0F, 0.5D, 0.7D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Vindicator.class, 8.0F, 0.5D, 0.7D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Vex.class, 8.0F, 0.5D, 0.7D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Pillager.class, 15.0F, 0.5D, 0.7D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Illusioner.class, 12.0F, 0.5D, 0.7D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Sheep.class, 8.0F, 0.4D, 0.6D));
        this.goalSelector.addGoal(1, new GolemFleeEntityGoal<>(this, Cow.class, 8.0F, 0.4D, 0.6D));
        this.goalSelector.addGoal(1, new GolemPanicGoal(this));
        this.goalSelector.addGoal(2, new GolemTemptGoal(this));
        this.goalSelector.addGoal(2, new GolemBeShyGoal(this));
        this.goalSelector.addGoal(3, new HarvestCropGoal(this));
        this.goalSelector.addGoal(3, new DeliverCropGoal(this));
        this.goalSelector.addGoal(5, new ReturnToTetherGoal(this));
        this.goalSelector.addGoal(6, new GolemWanderGoal(this));
        if (Services.PLATFORM.isModLoaded("animal_feeding_trough")) {
            this.goalSelector.addGoal(6, new GolemRepairSelfGoal(this, 24));
        }
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public boolean isScared() {
        return entityData.get(IS_SCARED);
    }

    public void setScared(boolean isScared) {
        this.entityData.set(IS_SCARED, isScared);
    }

    /* Base Logic */

    @Override
    public void baseTick() {
        super.baseTick();
        if (level.isClientSide) baseClientTick();
        else baseServerTick();
        baseCommonTick();
    }

    private void baseClientTick() {
    }

    private void baseServerTick() {
        getDecay().decay();
        if (isInWaterOrRain()) {
            if (isInWater()) {
                if (StrawgolemConfig.Lifespan.waterAcceleratesDecay.get()) getDecay().decay();
            } else if (!hasHat()) {
                if (StrawgolemConfig.Lifespan.rainAcceleratesDecay.get()) getDecay().decay();
            }
        }
    }

    private void baseCommonTick() {
        if (getDecay().getState() == DecayState.DYING && getRandom().nextInt(StrawgolemConfig.Visual.dyingGolemFlyChance.get()) == 0) {
            spawnFlyParticle();
        }
    }

    @Override
    public void moveTo(double $$0, double $$1, double $$2, float $$3, float $$4) {
        super.moveTo($$0, $$1, $$2, $$3, $$4);
        // Initially set tether
        if (!tether.exists()) {
            tether.update();
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if (item.getItem() == REPAIR_ITEM && decay.getState() != DecayState.NEW) {
            boolean success = decay.repair();
            if (success) {
                spawnHappyParticle();
                item.shrink(1);
            }
            return InteractionResult.CONSUME;
        } else if (item.getItem() == StrawgolemItems.strawHat.get() && !hasHat()) {
            this.entityData.set(HAS_HAT, true);
            item.shrink(1);
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    /* Damage */

    @Override
    public boolean isDamageSourceBlocked(DamageSource source) {
        if (source == DamageSource.SWEET_BERRY_BUSH) return true;
        return super.isDamageSourceBlocked(source);
    }

    @Override
    protected void actuallyHurt(DamageSource $$0, float $$1) {
        super.actuallyHurt($$0, $$1);
        decay.setFromHealth();
    }

    /* Items */

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        spawnAtLocation(getHeldItem().get().copy());
        getHeldItem().get().setCount(0);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) return heldItem.get();
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            heldItem.set(stack);
        }
    }

    /* Animations */

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new StrawgolemMovementController(this));
        data.addAnimationController(new StrawgolemArmsController(this));
        data.addAnimationController(new StrawgolemHarvestController(this));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private double getSqrMovement() {
        double xDiff = getX() - xOld;
        double zDiff = getZ() - zOld;
        return xDiff * xDiff + zDiff * zDiff;
    }

    public boolean isRunning() {
        return getSqrMovement() >= RUN_DISTANCE;
    }

    public boolean isMoving() {
        return getSqrMovement() >= WALK_DISTANCE;
    }

    public boolean isInCold() {
        return level.getBiome(blockPosition()).value().getBaseTemperature() < 0.15F;
    }


    /* Capabilities */

    @Override
    public CapabilityManager getCapabilityManager() {
        return capabilities;
    }

    public Decay getDecay() {
        return decay;
    }

    public HeldItem getHeldItem() {
        return heldItem;
    }

    public Harvester getHarvester() {
        return harvester;
    }

    public Deliverer getDeliverer() {
        return deliverer;
    }

    public Tether getTether() {
        return tether;
    }

    public boolean hasHat() {
        return this.entityData.get(HAS_HAT);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Hat!
        this.entityData.set(HAS_HAT, tag.getBoolean("hasHat"));
        // We don't actually want harvest capability to persist
        harvester.clear();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("hasHat", this.hasHat());
        super.addAdditionalSaveData(tag);
    }

    /* Ambience */

    @Override
    protected SoundEvent getAmbientSound() {
        if (isRunningGoal(PanicGoal.class, AvoidEntityGoal.class)) return StrawgolemSounds.GOLEM_SCARED.get();
        if (isHoldingBlock()) return StrawgolemSounds.GOLEM_STRAINED.get();
        return StrawgolemSounds.GOLEM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return StrawgolemSounds.GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return StrawgolemSounds.GOLEM_DEATH.get();
    }

    /* Helpers */

    public boolean isHoldingBlock() {
        Item item = heldItem.get().getItem();
        return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof StemGrownBlock;
    }

    @SafeVarargs
    public final boolean isRunningGoal(Class<? extends Goal>... classes) {
        return goalSelector.getRunningGoals().anyMatch(goal -> {
            for (Class<? extends Goal> clazz : classes) {
                if (clazz.isInstance(goal.getGoal())) return true;
            }
            return false;
        });
    }

    private void spawnFlyParticle() {
        Vec3 pos = position();
        Vec3 movement = getDeltaMovement();
        level.addParticle(StrawgolemParticles.FLY_PARTICLE.get(), pos.x, pos.y + 0.15F, pos.z, movement.x, movement.y + 0.15F, movement.z);
    }

    private void spawnHappyParticle() {
        Vec3 pos = position();
        Vec3 movement = getDeltaMovement();
        double x = random.nextFloat() + pos.x - 0.5F;
        double z = random.nextFloat() + pos.z - 0.5F;
        level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, pos.y + 0.85F, z, movement.x, movement.y, movement.z);
    }

}
