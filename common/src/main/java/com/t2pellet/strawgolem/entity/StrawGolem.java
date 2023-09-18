package com.t2pellet.strawgolem.entity;

import com.t2pellet.strawgolem.StrawgolemSounds;
import com.t2pellet.strawgolem.entity.animations.StrawgolemIdleController;
import com.t2pellet.strawgolem.entity.animations.StrawgolemItemController;
import com.t2pellet.strawgolem.entity.animations.StrawgolemWalkController;
import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.entity.capabilities.harvester.Harvester;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import com.t2pellet.strawgolem.entity.capabilities.tether.Tether;
import com.t2pellet.strawgolem.entity.goals.*;
import com.t2pellet.tlib.Services;
import com.t2pellet.tlib.common.entity.capability.CapabilityManager;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StemGrownBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

// TODO : Decay fly
// TODO : Finish animations
// TODO : Config
// TODO : Straw hat
// TODO : Fix bug - Animation transition on world load
// TODO : Fix bug - walking animation jank
// TODO : Fix bug - not always walking fully to destination
public class StrawGolem extends AbstractGolem implements IAnimatable, ICapabilityHaver {

    private static final double STOP_DISTANCE = 0.00001D;
    private static final double WALK_DISTANCE = 0.00007D;
    private static final double RUN_DISTANCE = 0.004D;

    // Capabilities
    CapabilityManager capabilities = CapabilityManager.newInstance(this);
    private final Decay decay;
    private final HeldItem heldItem;
    private final Harvester harvester;
    private final Deliverer deliverer;
    private final Tether tether;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.MAX_HEALTH, 5.0F);
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    protected StrawGolem(EntityType<? extends StrawGolem> type, Level level) {
        super(type, level);
        decay = capabilities.addCapability(Decay.class);
        heldItem = capabilities.addCapability(HeldItem.class);
        harvester = capabilities.addCapability(Harvester.class);
        deliverer = capabilities.addCapability(Deliverer.class);
        tether = capabilities.addCapability(Tether.class);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Evoker.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vindicator.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vex.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Pillager.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Illusioner.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zoglin.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Sheep.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Cow.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.8D));
        this.goalSelector.addGoal(2, new HarvestCropGoal(this, 24));
        this.goalSelector.addGoal(2, new DeliverCropGoal(this, 24));
        this.goalSelector.addGoal(3, new ReturnToTetherGoal(this));
        this.goalSelector.addGoal(4, new GolemWanderGoal(this));
        if (Services.PLATFORM.isModLoaded("animal_feeding_trough")) {
            this.goalSelector.addGoal(4, new GolemRepairSelfGoal(this, 24));
        }
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        getDecay().decay();
        if (isInWaterRainOrBubble()) getDecay().decay();
    }

    @Override
    public void moveTo(double $$0, double $$1, double $$2, float $$3, float $$4) {
        super.moveTo($$0, $$1, $$2, $$3, $$4);
        if (!tether.exists()) {
            tether.update();
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if (item.getItem() == Items.WHEAT && decay.getState() != DecayState.NEW) {
            boolean success = decay.repair();
            if (success) item.shrink(1);
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        spawnAtLocation(getHeldItem().get().copy());
        getHeldItem().get().setCount(0);
    }

    @Override
    public boolean isDamageSourceBlocked(DamageSource source) {
        if (source == DamageSource.SWEET_BERRY_BUSH) return true;
        return super.isDamageSourceBlocked(source);
    }

    /* Item */

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
        data.addAnimationController(new StrawgolemWalkController(this));
        data.addAnimationController(new StrawgolemIdleController(this));
        data.addAnimationController(new StrawgolemItemController(this));
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

    public boolean isStopped() {
        return getSqrMovement() < STOP_DISTANCE;
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

    /* Ambience */

    @Override
    protected SoundEvent getAmbientSound() {
        if (isRunningGoal(PanicGoal.class, AvoidEntityGoal.class)) return StrawgolemSounds.GOLEM_SCARED;
        if (isHoldingBlock()) return StrawgolemSounds.GOLEM_STRAINED;
        return StrawgolemSounds.GOLEM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return StrawgolemSounds.GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return StrawgolemSounds.GOLEM_DEATH;
    }

    /* Helpers */

    public boolean isHoldingBlock() {
        Item item = heldItem.get().getItem();
        return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof StemGrownBlock;
    }

    @SafeVarargs
    private boolean isRunningGoal(Class<? extends Goal> ...classes) {
        return goalSelector.getRunningGoals().anyMatch(goal -> {
            for (Class<? extends Goal> clazz : classes) {
                if (clazz.isInstance(goal.getGoal())) return true;
            }
            return false;
        });
    }

}
