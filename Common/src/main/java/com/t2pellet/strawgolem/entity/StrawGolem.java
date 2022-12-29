package com.t2pellet.strawgolem.entity;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.crop.CropRegistry;
import com.t2pellet.strawgolem.crop.WorldCrops;
import com.t2pellet.strawgolem.entity.ai.*;
import com.t2pellet.strawgolem.entity.capability.CapabilityManager;
import com.t2pellet.strawgolem.entity.capability.accessory.Accessory;
import com.t2pellet.strawgolem.entity.capability.hunger.Hunger;
import com.t2pellet.strawgolem.entity.capability.hunger.IHasHunger;
import com.t2pellet.strawgolem.entity.capability.lifespan.Lifespan;
import com.t2pellet.strawgolem.entity.capability.memory.Memory;
import com.t2pellet.strawgolem.entity.capability.tether.IHasTether;
import com.t2pellet.strawgolem.entity.capability.tether.Tether;
import com.t2pellet.strawgolem.events.WorldInteractHandler;
import com.t2pellet.strawgolem.network.CapabilityPacket;
import com.t2pellet.strawgolem.platform.Services;
import com.t2pellet.strawgolem.registry.CommonRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.Arrays;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.*;

public class StrawGolem extends AbstractGolem implements IHasHunger, IHasTether {

    private static final ResourceLocation ResourceLocation = new ResourceLocation(StrawgolemCommon.MODID, "strawgolem");
    private static final int maxLifespan = StrawgolemConfig.Health.getLifespan() + StrawgolemConfig.Health.getWheatTicks();
    private static final int maxHunger = StrawgolemConfig.Health.getHunger() + StrawgolemConfig.Health.getFoodTicks();
    private static final float maxHealth = 8.0F;
    private static final float moveSpeed = 0.2F;

    private final CapabilityManager capabilities;
    private final SimpleContainer inventory;
    private boolean tempted;
    private boolean isNew;

    public BlockPos harvestPos;

    public static AttributeSupplier.Builder createMob() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.MOVEMENT_SPEED, moveSpeed);
    }

    public StrawGolem(EntityType<? extends StrawGolem> type, Level levelIn) {
        super(type, levelIn);
        capabilities = CapabilityManager.newInstance();
        capabilities.addCapability(Lifespan.class);
        capabilities.addCapability(Memory.class);
        capabilities.addCapability(Tether.class);
        capabilities.addCapability(Hunger.class);
        capabilities.addCapability(Accessory.class);
        inventory = new SimpleContainer(1);
        harvestPos = null;
        tempted = false;
        isNew = true;
    }

    @Override
    protected void registerGoals() {
        int priority = 0;
        this.goalSelector.addGoal(++priority, new GolemFleeGoal(this));
        this.goalSelector.addGoal(++priority, new GolemTemptGoal(this));
        this.goalSelector.addGoal(++priority, new GolemPoutGoal(this));
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
        return goalSelector.getRunningGoals().anyMatch(goal -> Arrays.stream(clazzes).anyMatch(clazz -> clazz.isInstance(goal.getGoal())));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level.isClientSide()) {
            // Profile on first spawn
            if (isNew) {
                profileCrops();
                isNew = false;
            }

            // Calculate lifespan and hunger decrease
            if (StrawgolemConfig.Health.getLifespan() > 0) {
                int lifeTicks = 1;
                int hungerTicks = 1;
                if (holdingFullBlock() && StrawgolemConfig.Health.isHeavyPenalty()) {
                    ++lifeTicks;
                    ++hungerTicks;
                }
                if (isInWaterOrBubble() && StrawgolemConfig.Health.isWaterPenalty()) {
                    ++lifeTicks;
                } else if (!getAccessory().hasHat() && isInWaterOrRain() && StrawgolemConfig.Health.isRainPenalty()) {
                    ++lifeTicks;
                }
                getLifespan().shrink(lifeTicks);
                getHunger().shrink(hungerTicks);
                // Update health and speed
                float curMaxHealth = maxHealth * Math.max(0.25F, Math.min(1.25F, getLifespan().getPercentage()));
                float curMoveSpeed = moveSpeed * Math.max(0.5F, Math.min(1.25F, getHunger().getPercentage()));
                getAttribute(Attributes.MAX_HEALTH).setBaseValue(curMaxHealth);
                getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(curMoveSpeed);
                // Kill if appropriate
                if (getLifespan().isOver()) {
                    kill();
                }
            }
            // Capability packet update
            if (random.nextInt(40) == 0) {
                Services.PACKETS.sendInRange(new CapabilityPacket(this), this, 25.0F);
            }
        }
        // Fly particle
        if (StrawgolemConfig.Health.getLifespan() > 0 && getLifespan().get() * 4 < StrawgolemConfig.Health.getLifespan() && random.nextInt(240) == 0) {
            spawnParticle(CommonRegistry.Particles.getFlyParticle());
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
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        Item heldItem = player.getItemInHand(hand).getItem();

        if (heldItem == Items.WHEAT) {
            // Condition
            int newLifespan = getLifespan().get() + StrawgolemConfig.Health.getWheatTicks();
            if (getHealth() == getMaxHealth() && newLifespan > maxLifespan) {
                return InteractionResult.FAIL;
            }
            // Compute
            if (!level.isClientSide()) {
                heal(0.5F);
                if (StrawgolemConfig.Health.getLifespan() > -1 && newLifespan < maxLifespan) {
                    getLifespan().set(newLifespan);
                }
                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }
                Services.PACKETS.sendInRange(new CapabilityPacket(this), this, 25.0F);
                // Feedback
                playSound(GOLEM_HEAL, 1.0F, 1.0F);
                playSound(SoundEvents.GRASS_STEP, 1.0F, 1.0F);
            }
            spawnParticle(ParticleTypes.HEART);
            // Result
            return InteractionResult.CONSUME;
        } else if (heldItem == CommonRegistry.Items.getStrawHat()) {
            // Check condition
            if (getAccessory().hasHat()) {
                return InteractionResult.FAIL;
            }
            // Compute
            if (!level.isClientSide()) {
                getAccessory().setHasHat(true);
                if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
                Services.PACKETS.sendInRange(new CapabilityPacket(this), this, 25.0F);
                // Feedback
                playSound(GOLEM_INTERESTED, 1.0F, 1.0F);
                playSound(SoundEvents.GRASS_STEP, 1.0F, 1.0F);
            }
            // Result
            return InteractionResult.CONSUME;
        } else if (heldItem == StrawgolemConfig.Health.getFoodItem()) {
            // Check condition
            int newHunger = getHunger().get() + StrawgolemConfig.Health.getFoodTicks();
            if (newHunger > maxHunger) {
                return InteractionResult.FAIL;
            }
            // Compute
            if (!level.isClientSide()) {
                if (StrawgolemConfig.Health.getHunger() > -1 && newHunger < maxHunger) {
                    getHunger().set(newHunger);
                }
                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }
                Services.PACKETS.sendInRange(new CapabilityPacket(this), this, 25.0F);
                // Feedback
                playSound(GOLEM_HEAL, 1.0F, 1.0F);
            }
            spawnParticle(ParticleTypes.HAPPY_VILLAGER);
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

    private void spawnParticle(ParticleOptions type) {
        Vec3 deltaMovement = this.getDeltaMovement();
        level.addParticle(type,
                getX() + random.nextDouble() - 0.5, getY() + 0.4D, getZ() + random.nextDouble() - 0.5,
                deltaMovement.x, deltaMovement.y, deltaMovement.z);
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt bolt) {
        if (random.nextInt(10) == 0 && !level.isClientSide()) {
            StrawngGolem strawngGolem = CommonRegistry.Entities.getStrawngGolemType().create(level);
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
    protected void actuallyHurt(DamageSource source, float $$1) {
        super.actuallyHurt(source, $$1);
        // Profile nearby crops when hit and idling
        if (source.getDirectEntity() instanceof Player) {
            profileCrops();
        }
    }

    private void profileCrops() {
        BlockPos currPos;
        int maxRange = StrawgolemConfig.Harvest.getSearchRange();
        for (int i = -maxRange; i < maxRange; ++i) {
            for (int j = -maxRange; j < maxRange; ++j) {
                for (int k = -maxRange; k < maxRange; ++k) {
                    currPos = blockPosition().offset(i, j, k);
                    if (CropRegistry.INSTANCE.isGrownCrop(level, currPos)) {
                        WorldCrops.of(level).addCrop(currPos);
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
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.SWEET_BERRY_BUSH) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean isInWall() {
        if (this.noPhysics) {
            return false;
        } else {
            float scaledWidth = this.getDimensions(Pose.STANDING).width * 0.8F;
            AABB headBounds = AABB.ofSize(this.getEyePosition(), scaledWidth, 1.0E-6D, scaledWidth);
            // Don't suffocate from stem grown blocks
            return BlockPos.betweenClosedStream(headBounds).anyMatch((pos) -> {
                BlockState state = this.level.getBlockState(pos);
                return !state.isAir() && state.isSuffocating(this.level, pos) && Shapes.joinIsNotEmpty(state.getCollisionShape(this.level, pos).move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(headBounds), BooleanOp.AND) && !(state.getBlock() instanceof StemGrownBlock);
            });
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingMultiplier, boolean allowDrops) {
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
        if (levelIn != this.level) {
            return false;
        } else {
            if (pos.distManhattan(pos) > 1.27 * StrawgolemConfig.Harvest.getSearchRange()) { // on avg, manhattan dist is 4/pi times actual distance
                return false;
            } else {
                Vec3 eyePos = Vec3.atCenterOf(blockPosition()).add(0.0D, 0.5D, 0.0D);
                Vec3 blockPos = Vec3.atCenterOf(pos);
                BlockHitResult result = this.level.clip(new ClipContext(eyePos, blockPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                return result.getType() == HitResult.Type.MISS || result.getBlockPos().closerThan(pos, 1.0D);
            }
        }
    }

    public boolean isHarvesting() {
        return harvestPos != null;
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
    public ItemStack getItemBySlot(EquipmentSlot slot) {
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

    /**
     * @return the CapabilityManager for the straw golem
     */
    public CapabilityManager getCapabilityManager() {
        return capabilities;
    }

    public Lifespan getLifespan() {
        return capabilities.getCapability(Lifespan.class);
    }

    /**
     * Returns the memory, capability, used to store and retrieve chest positions
     *
     * @return the golem's memory capability
     */
    public Memory getMemory() {
        return capabilities.getCapability(Memory.class);
    }

    /**
     * Returns the tether capability, used to prevent the golem from wandering too far
     * @return the golem's tether capability
     */
    @Override
    public Tether getTether() {
        return capabilities.getCapability(Tether.class);
    }

    /**
     * Returns the hunger capability, used to remember if the golem needs to eat!
     */
    @Override
    public Hunger getHunger() {
        return capabilities.getCapability(Hunger.class);
    }

    /**
     * Returns the accessory capability, used to store which accessories the golem is wearing
     */
    public Accessory getAccessory() {
        return capabilities.getCapability(Accessory.class);
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
        if (getVehicle() instanceof IronGolem || getVehicle() instanceof StrawngGolem) {
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
        if (getVehicle() instanceof IronGolem || getVehicle() instanceof StrawngGolem) {
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
        tag.put("inventory", inventory.createTag());
        tag.put("capabilities", capabilities.writeTag());
        if (harvestPos != null) tag.put("harvestPos", NbtUtils.writeBlockPos(harvestPos));
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("inventory")) inventory.fromTag((ListTag) tag.get("inventory"));
        if (tag.contains("capabilities")) capabilities.readTag(tag.get("capabilities"));
        if (tag.contains("harvestPos")) harvestPos = NbtUtils.readBlockPos((CompoundTag) tag.get("harvestPos"));
        super.readAdditionalSaveData(tag);
    }


    /* Sounds */

    @Override
    protected SoundEvent getAmbientSound() {
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) {
            if (goalSelector.getRunningGoals().anyMatch(
                    goal -> goal.getGoal() instanceof GolemFleeGoal || goal.getGoal() instanceof GolemTetherGoal))
                return GOLEM_SCARED;
            else if (holdingFullBlock() || getHunger().isHungry()) return GOLEM_STRAINED;
            return GOLEM_AMBIENT;
        }
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
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
