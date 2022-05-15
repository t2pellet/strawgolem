package com.t2pellet.strawgolem.entity;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.crop.CropHandler;
import com.t2pellet.strawgolem.crop.CropValidator;
import com.t2pellet.strawgolem.entity.ai.*;
import com.t2pellet.strawgolem.entity.capability.CapabilityHandler;
import com.t2pellet.strawgolem.entity.capability.hunger.Hunger;
import com.t2pellet.strawgolem.entity.capability.hunger.IHasHunger;
import com.t2pellet.strawgolem.entity.capability.lifespan.Lifespan;
import com.t2pellet.strawgolem.entity.capability.memory.Memory;
import com.t2pellet.strawgolem.entity.capability.tether.IHasTether;
import com.t2pellet.strawgolem.entity.capability.tether.Tether;
import com.t2pellet.strawgolem.events.WorldInteractHandler;
import com.t2pellet.strawgolem.network.HealthPacket;
import com.t2pellet.strawgolem.platform.Services;
import com.t2pellet.strawgolem.registry.CommonRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Particles.getFlyParticle;

public class EntityStrawGolem extends AbstractGolem implements IHasHunger, IHasTether {
    public static final SoundEvent GOLEM_AMBIENT = new SoundEvent(CommonRegistry.Sounds.GOLEM_AMBIENT_ID);
    public static final SoundEvent GOLEM_STRAINED = new SoundEvent(CommonRegistry.Sounds.GOLEM_STRAINED_ID);
    public static final SoundEvent GOLEM_HURT = new SoundEvent(CommonRegistry.Sounds.GOLEM_HURT_ID);
    public static final SoundEvent GOLEM_DEATH = new SoundEvent(CommonRegistry.Sounds.GOLEM_DEATH_ID);
    public static final SoundEvent GOLEM_HEAL = new SoundEvent(CommonRegistry.Sounds.GOLEM_HEAL_ID);
    public static final SoundEvent GOLEM_SCARED = new SoundEvent(CommonRegistry.Sounds.GOLEM_SCARED_ID);
    public static final SoundEvent GOLEM_INTERESTED = new SoundEvent(CommonRegistry.Sounds.GOLEM_INTERESTED_ID);

    private static final ResourceLocation ResourceLocation = new ResourceLocation(StrawgolemCommon.MODID, "strawgolem");
    private static final int maxLifespan = StrawgolemConfig.Health.getLifespan() + 12000;
    private static final int maxHunger = StrawgolemConfig.Health.getHunger() + 6000;

    private final Lifespan lifespan;
    private final Memory memory;
    private final SimpleContainer inventory;
    private final Tether tether;
    private final Hunger hunger;
    private boolean tempted;

    public static AttributeSupplier.Builder createMob() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    public EntityStrawGolem(EntityType<? extends EntityStrawGolem> type, Level levelIn) {
        super(type, levelIn);
        lifespan = CapabilityHandler.INSTANCE.get(Lifespan.class).orElseThrow(() -> new InstantiationError("Failed to create lifespan cap"));
        memory = CapabilityHandler.INSTANCE.get(Memory.class).orElseThrow(() -> new InstantiationError("Failed to create memory cap"));
        tether = CapabilityHandler.INSTANCE.get(Tether.class).orElseThrow(() -> new InstantiationError("Failed to create tether cap"));
        hunger = CapabilityHandler.INSTANCE.get(Hunger.class).orElseThrow(() -> new InstantiationError("Failed to create new hunger cap"));
        inventory = new SimpleContainer(1);
        tempted = false;
        // Set default tether value
    }

    @Override
    protected void registerGoals() {
        int priority = 0;
        this.goalSelector.addGoal(++priority, new GolemPoutGoal(this));
        this.goalSelector.addGoal(++priority, new GolemFleeGoal(this));
        this.goalSelector.addGoal(++priority, new GolemTemptGoal(this));
        this.goalSelector.addGoal(++priority, new GolemHarvestGoal(this));
        this.goalSelector.addGoal(++priority, new GolemDeliverGoal(this));
        if (StrawgolemConfig.Tether.isTetherEnabled()) {
            this.goalSelector.addGoal(++priority, new GolemTetherGoal<>(this, 0.8D));
        }
        this.goalSelector.addGoal(++priority, new GolemWanderGoal(this));
        this.goalSelector.addGoal(++priority, new GolemLookAtPlayerGoal(this, 4.0F));
        this.goalSelector.addGoal(++priority, new GolemLookRandomlyGoal(this));
    }

    @SafeVarargs
    public final boolean isRunningGoal(Class<? extends Goal>... clazzes) {
        return goalSelector.getRunningGoals().anyMatch(goal -> Arrays.stream(clazzes).anyMatch(clazz -> clazz.isInstance(goal)));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level.isClientSide) {
            lifespan.update();
            hunger.update();
            float healthCap = 8.0F * Math.round((float) lifespan.get() / StrawgolemConfig.Health.getLifespan());
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(healthCap);
            if (getHealth() > healthCap) setHealth(healthCap);
            if (holdingFullBlock() && StrawgolemConfig.Health.isHeavyPenalty()) {
                lifespan.update();
                hunger.update();
            }
            if (isInWaterOrRain() && !isInWater() && StrawgolemConfig.Health.isRainPenalty()) {
                lifespan.update();
            }
            if (isInWaterOrBubble() && StrawgolemConfig.Health.isWaterPenalty()) {
                lifespan.update();
            }
            if (random.nextInt(40) == 0) {
                Services.PACKETS.sendInRange(new HealthPacket(this), this, 25.0F);
            }
            if (lifespan.isOver()) {
                hurt(DamageSource.MAGIC, getMaxHealth() * 100);
            }
            if (StrawgolemConfig.Health.getHunger() > 0 && hunger.get() * 4 < StrawgolemConfig.Health.getHunger() && random.nextInt(120) == 0) {
                playSound(GOLEM_STRAINED, 1.0F, 1.0F);
            }
        } else if (StrawgolemConfig.Health.getLifespan() > 0 && lifespan.get() * 4 < StrawgolemConfig.Health.getLifespan() && random.nextInt(80) == 0) {
            level.addParticle((ParticleOptions) getFlyParticle(), getX(), getY(), getZ(),
                    0, 0, 0);
        }
    }

    /**
     * Determines if the golem is in the cold
     * @return true if the golem is in the cold, false otherwise
     */
    public boolean isInCold() {
        return level.getBiome(blockPosition()).value().getBaseTemperature() < 0.15F;
    }

    /* Interaction */

    @Override
    protected  InteractionResult mobInteract(Player player,  InteractionHand hand) {
        Item heldItem = player.getItemInHand(hand).getItem();

        if (heldItem == Items.WHEAT) {
            // Check condition
            int newLifespan = lifespan.get() + 12000;
            if (newLifespan > maxLifespan) {
                setHealth(getHealth() + 0.5F);
                return InteractionResult.FAIL;
            }
            // Compute
            if (!level.isClientSide()) {
                setHealth(getHealth() + 0.5F);
                if (StrawgolemConfig.Health.getLifespan() > -1) lifespan.set(newLifespan);
                if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                Services.PACKETS.sendInRange(new HealthPacket(this), this, 25.0F);
                // Feedback
                playSound(GOLEM_HEAL, 1.0F, 1.0F);
                playSound(SoundEvents.GRASS_STEP, 1.0F, 1.0F);
            }
            spawnHealParticles(getX(), getY(), getZ());
            // Result
            return InteractionResult.CONSUME;
        } else if (heldItem == Items.APPLE) {
            // Check condition
            int newHunger = hunger.get() + 6000;
            if (newHunger > maxHunger) return InteractionResult.FAIL;
            // Compute
            if (!level.isClientSide()) {
                hunger.set(newHunger);
                if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                Services.PACKETS.sendInRange(new HealthPacket(this), this,25.0F);
                // Feedback
                playSound(GOLEM_HEAL, 1.0F, 1.0F);
            }
            spawnHappyParticles(getX(), getY(), getZ());
            // Result
            return InteractionResult.CONSUME;
        } else if (heldItem == Items.AIR) {
            // Condition
            if (hand == InteractionHand.OFF_HAND || !player.isShiftKeyDown()) return InteractionResult.FAIL;
            // Compute
            if (!level.isClientSide()) {
                WorldInteractHandler.addMapping(player.getUUID(), getId());
                // Feedback
                Component message = new TranslatableComponent("strawgolem.order", getDisplayName().getString());
                player.displayClientMessage(message, true);
            }
            // Result
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private void spawnHealParticles(double x, double y, double z) {
        level.addParticle(
                ParticleTypes.HEART,
                x + random.nextDouble() - 0.5, y + 0.4D, z + random.nextDouble() - 0.5,
                this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    }

    private void spawnHappyParticles(double x, double y, double z) {
        level.addParticle(
                ParticleTypes.HAPPY_VILLAGER,
                x + random.nextDouble() - 0.5, y + 0.4D, z + random.nextDouble() - 0.5,
                this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    }

    @Override
    public void thunderHit( ServerLevel world,  LightningBolt bolt) {
        if (random.nextInt(10) == 0 && !level.isClientSide()) {
            EntityStrawngGolem strawngGolem = CommonRegistry.Entities.getStrawngGolemType().create(level);
            if (hasCustomName()) {
                strawngGolem.setCustomName(getCustomName());
            }
            strawngGolem.setPos(position());
            strawngGolem.setYHeadRot(yHeadRot);
            strawngGolem.setXRot(getXRot());
            strawngGolem.setPos(xOld, yOld, zOld);
            remove(RemovalReason.DISCARDED);
            if (level.getBlockState(blockPosition()).getBlock() == Blocks.FIRE) {
                level.setBlockAndUpdate(blockPosition(), Blocks.AIR.defaultBlockState());
            }
            level.addFreshEntity(strawngGolem);
        }
    }

    @Override
    protected void actuallyHurt( DamageSource source, float $$1) {
        super.actuallyHurt(source, $$1);
        // Profile nearby crops when hit and idling
        if (source.getDirectEntity() instanceof Player && !isRunningGoal(GolemHarvestGoal.class, GolemDeliverGoal.class, GolemFleeGoal.class, GolemTemptGoal.class, GolemPoutGoal.class, GolemTetherGoal.class)) {
            BlockPos currPos;
            int maxRange = StrawgolemConfig.Harvest.getSearchRange();
            for (int i = -maxRange; i < maxRange; ++i) {
                for (int j = -maxRange; j < maxRange; ++j) {
                    for (int k = -maxRange; k < maxRange; ++k) {
                        currPos = blockPosition().offset(i, j, k);
                        if (CropValidator.isGrownCrop(level.getBlockState(currPos))) {
                            CropHandler.INSTANCE.addCrop(level, currPos);
                        }
                    }
                }
            }
        }
    }

    /* Death & Despawning */

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    public boolean hurt( DamageSource source, float amount) {
        if (source == DamageSource.SWEET_BERRY_BUSH) return false;
        return super.hurt(source, amount);
    }

    @Override
    protected void dropCustomDeathLoot( DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropCustomDeathLoot(source, lootingMultiplier, allowDrops);
        if (!level.isClientSide()) {
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), inventory.getItem(0).copy()));
        }
    }

    /* Harvesting */

    /**
     * Checks if golem has line of sight on the block
     * @param levelIn the level
     * @param pos     the position
     * @return whether the golem has line of sight
     */
    public boolean canReachBlock(LevelReader levelIn, BlockPos pos) {
        if (levelIn.dimensionType() == level.dimensionType()) {
            return navigation.createPath(pos.offset(0, 0.5, 0), StrawgolemConfig.Harvest.getSearchRange()) != null;
        }
        return false;
    }
    
    public boolean isHarvesting() {
        return isRunningGoal(GolemHarvestGoal.class);
    }

    /* Handle inventory */

    public SimpleContainer getInventory() {
        return inventory;
    }

    /**
     * Returns true if the golem is not holding anything, and false otherwise
     * @return whether the hand is empty
     */
    public boolean isHandEmpty() {
        return getMainHandItem().isEmpty();
    }

    @Override
    public  ItemStack getItemBySlot( EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return inventory.getItem(0);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns true if the golem is holding a block crop, and false otherwise
     * @return whether the golem is holding a gourd block
     */
    public boolean holdingFullBlock() {
        ItemStack item = getMainHandItem();
        if (!(item.getItem() instanceof BlockItem blockItem)) return false;
        return blockItem != Items.AIR
                && blockItem.getBlock().defaultBlockState().canOcclude()
                && blockItem.getBlock().asItem() == blockItem;
    }

    /* Handles capabilities */

    public Lifespan getLifespan() {
        return lifespan;
    }

    /**
     * Returns the memory, capability, used to store and retrieve chest positions
     * @return the golem's memory capability
     */
    public Memory getMemory() {
        return memory;
    }

    /**
     * Returns the tether capability, used to prevent the golem from wandering too far
     * @return the golem's tether capability
     */
    @Override
    public Tether getTether() {
        return tether;
    }

    /**
     * Returns the hunger capability, used to remember if the golem needs to eat!
     */
    @Override
    public Hunger getHunger() {
        return hunger;
    }

    @Override
    public void setTempted(boolean tempted) {
        this.tempted = tempted;
    }

    @Override
    public boolean isTempted() {
        return tempted;
    }

    /* Golem Pickup */

    @Override
    public void setPos(double posX, double posY, double posZ) {
        if (getVehicle() instanceof IronGolem || getVehicle() instanceof EntityStrawngGolem) {
            AbstractGolem golemEntity = (AbstractGolem) getVehicle();
            double lookX = golemEntity.getLookAngle().x;
            double lookZ = golemEntity.getLookAngle().z;
            double magnitude = Math.sqrt(lookX * lookX + lookZ * lookZ);
            lookX /= magnitude;
            lookZ /= magnitude;
            super.setPos(posX + 1.71D * lookX, posY - 0.55D, posZ + 1.71D * lookZ);
        } else {
            super.setPos(posX, posY, posZ);
        }
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        if (getVehicle() instanceof IronGolem || getVehicle() instanceof EntityStrawngGolem) {
            LivingEntity ridingEntity = (LivingEntity) getVehicle();
            double lookX = ridingEntity.getLookAngle().x;
            double lookZ = ridingEntity.getLookAngle().z;
            double magnitude = Math.sqrt(lookX * lookX + lookZ * lookZ);
            lookX /= magnitude;
            lookZ /= magnitude;
            super.setPos(getX() + lookX, getY(), getZ() + lookZ);
        }
    }

    /* Storage */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.put("lifespan", lifespan.writeTag());
        tag.put("hunger", hunger.writeTag());
        tag.put("memory", memory.writeTag());
        tag.put("inventory", inventory.createTag());
        tag.put("tether", tether.writeTag());
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("lifespan")) lifespan.readTag(tag.get("lifespan"));
        if (tag.contains("hunger")) hunger.readTag(tag.get("hunger"));
        if (tag.contains("memory")) memory.readTag(tag.get("memory"));
        if (tag.contains("inventory")) inventory.fromTag((ListTag) tag.get("inventory"));
        if (tag.contains("tether")) tether.readTag(tag.get("tether"));
        super.readAdditionalSaveData(tag);
    }


    /* Sounds */

    @Override
    protected SoundEvent getAmbientSound() {
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) {
            if (goalSelector.getRunningGoals().anyMatch(
                    goal -> goal.getGoal() instanceof GolemFleeGoal || goal.getGoal() instanceof GolemTetherGoal))
                return GOLEM_SCARED;
            else if (holdingFullBlock()) return GOLEM_STRAINED;
            return GOLEM_AMBIENT;
        }
        return null;
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource damageSourceIn) {
        return StrawgolemConfig.Miscellaneous.isSoundsEnabled() ? GOLEM_HURT : null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return StrawgolemConfig.Miscellaneous.isSoundsEnabled() ? GOLEM_DEATH : null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return holdingFullBlock() ? 60 : 120;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return ResourceLocation;
    }

}
