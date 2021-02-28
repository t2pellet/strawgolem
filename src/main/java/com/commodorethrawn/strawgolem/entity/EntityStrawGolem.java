package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.ai.*;
import com.commodorethrawn.strawgolem.entity.capability.CapabilityHandler;
import com.commodorethrawn.strawgolem.entity.capability.hunger.Hunger;
import com.commodorethrawn.strawgolem.entity.capability.hunger.IHasHunger;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.Lifespan;
import com.commodorethrawn.strawgolem.entity.capability.memory.Memory;
import com.commodorethrawn.strawgolem.entity.capability.tether.IHasTether;
import com.commodorethrawn.strawgolem.entity.capability.tether.Tether;
import com.commodorethrawn.strawgolem.events.GolemChestHandler;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import com.commodorethrawn.strawgolem.registry.ClientRegistry;
import com.commodorethrawn.strawgolem.registry.StrawgolemSounds;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class EntityStrawGolem extends GolemEntity implements IHasHunger, IHasTether {
    public static final SoundEvent GOLEM_AMBIENT = StrawgolemSounds.GOLEM_AMBIENT.getSoundEvent();
    public static final SoundEvent GOLEM_STRAINED = StrawgolemSounds.GOLEM_STRAINED.getSoundEvent();
    public static final SoundEvent GOLEM_HURT = StrawgolemSounds.GOLEM_HURT.getSoundEvent();
    public static final SoundEvent GOLEM_DEATH = StrawgolemSounds.GOLEM_DEATH.getSoundEvent();
    public static final SoundEvent GOLEM_HEAL = StrawgolemSounds.GOLEM_HEAL.getSoundEvent();
    public static final SoundEvent GOLEM_SCARED = StrawgolemSounds.GOLEM_SCARED.getSoundEvent();
    public static final SoundEvent GOLEM_INTERESTED = StrawgolemSounds.GOLEM_INTERESTED.getSoundEvent();
    private static final Identifier LOOT = new Identifier(Strawgolem.MODID, "strawgolem");

    private final Lifespan lifespan;
    private final Memory memory;
    private final SimpleInventory inventory;
    private final Tether tether;
    private final Hunger hunger;
    private BlockPos harvestPos;

    public EntityStrawGolem(EntityType<? extends EntityStrawGolem> type, World worldIn) {
        super(type, worldIn);
        lifespan = CapabilityHandler.INSTANCE.get(Lifespan.class).orElseThrow(() -> new InstantiationError("Failed to create lifespan cap"));
        memory = CapabilityHandler.INSTANCE.get(Memory.class).orElseThrow(() -> new InstantiationError("Failed to create memory cap"));
        tether = CapabilityHandler.INSTANCE.get(Tether.class).orElseThrow(() -> new InstantiationError("Failed to create tether cap"));
        hunger = CapabilityHandler.INSTANCE.get(Hunger.class).orElseThrow(() -> new InstantiationError("Failed to create new hunger cap"));
        inventory = new SimpleInventory(1);
        harvestPos = BlockPos.ORIGIN;
    }

    @Override
    protected Identifier getLootTableId() {
        return LOOT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (ConfigHelper.isSoundsEnabled()) {
            if (goalSelector.getRunningGoals().anyMatch(
                    goal -> goal.getGoal() instanceof GolemFleeGoal || goal.getGoal() instanceof TetherGoal))
                return GOLEM_SCARED;
            else if (holdingFullBlock()) return GOLEM_STRAINED;
            return GOLEM_AMBIENT;
        }
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ConfigHelper.isSoundsEnabled() ? GOLEM_HURT : null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ConfigHelper.isSoundsEnabled() ? GOLEM_DEATH : null;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return holdingFullBlock() ? 60 : 120;
    }

    public static DefaultAttributeContainer.Builder createMob() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void initGoals() {
        int priority = 0;
        this.goalSelector.add(priority, new SwimGoal(this));
        this.goalSelector.add(++priority, new GolemPoutGoal(this));
        this.goalSelector.add(++priority, new GolemFleeGoal(this));
        this.goalSelector.add(++priority, new GolemTemptGoal(this));
        this.goalSelector.add(++priority, new GolemHarvestGoal(this, 0.6D));
        this.goalSelector.add(++priority, new GolemDeliverGoal(this, 0.6D));
        if (ConfigHelper.isTetherEnabled()) {
            this.goalSelector.add(++priority, new TetherGoal<>(this, 0.9D)); // tether is fast
        }
        this.goalSelector.add(++priority, new GolemWanderGoal(this, 0.6D));
        this.goalSelector.add(++priority, new GolemLookAtPlayerGoal(this, 4.0F));
        this.goalSelector.add(++priority, new GolemLookRandomlyGoal(this));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!world.isClient) {
            lifespan.update();
            hunger.update();
            float healthCap = Math.round((float) lifespan.get() / ConfigHelper.getLifespan()) * 4;
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(healthCap);
            if (getHealth() > healthCap) setHealth(healthCap);
            if (holdingFullBlock() && ConfigHelper.isLifespanPenalty("heavy")) {
                lifespan.update();
                hunger.update();
            }
            if (isInRain() && ConfigHelper.isLifespanPenalty("rain")) {
                lifespan.update();
            }
            if (isWet() && ConfigHelper.isLifespanPenalty("water")) {
                lifespan.update();
            }
            if (random.nextInt(40) == 0) {
                PacketHandler.sendHealthPacket(this);
            }
            if (lifespan.isOver()) {
                damage(DamageSource.MAGIC, getMaxHealth() * 100);
            }
            if (hunger.get() * 4 < ConfigHelper.getHunger() && hunger.get() > 0 && random.nextInt(120) == 0) playSound(GOLEM_STRAINED, 1.0F, 1.0F);
        } else if (lifespan.get() * 4 < ConfigHelper.getLifespan() && lifespan.get() >= 0 && random.nextInt(80) == 0) {
            world.addParticle(ClientRegistry.FLY_PARTICLE, prevX, prevY, prevZ,
                    0, 0, 0);
        }
    }

    /**
     * Determines if the golem is in the rain
     * @return true if the golem is in rain, false otherwise
     */
    public boolean isInRain() {
        return world.hasRain(getBlockPos())
                && world.isSkyVisible(getBlockPos())
                && ConfigHelper.isLifespanPenalty("rain");
    }

    /**
     * Determines if the golem is in the cold
     * @return true if the golem is in the cold, false otherwise
     */
    public boolean isInCold() {
        return world.getBiome(getBlockPos()).getTemperature(getBlockPos()) < 0.15F;
    }

    /* Handle inventory */

    /**
     * Returns true if the golem is not holding anything, and false otherwise
     *
     * @return whether the hand is empty
     */
    public boolean isHandEmpty() {
        return getHeldItem(Hand.MAIN_HAND).isEmpty();
    }

    public ItemStack getHeldItem(Hand hand) {
        return inventory.getStack(0);
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return inventory.getStack(0);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns true if the golem is holding a block crop, and false otherwise
     *
     * @return whether the golem is holding a gourd block
     */
    public boolean holdingFullBlock() {
        ItemStack item = getMainHandStack();
        if (!(item.getItem() instanceof BlockItem)) return false;
        BlockItem blockItem = (BlockItem) item.getItem();
        return blockItem != Items.AIR
                && blockItem.getBlock().getDefaultState().isOpaque()
                && blockItem.getBlock().asItem() == blockItem;
    }

    /* Miscellaneous */

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        if (!world.isClient) {
            dropStack(inventory.getStack(0).copy());
            inventory.getStack(0).setCount(0);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Item heldItem = player.getStackInHand(hand).getItem();
        if (Items.WHEAT == heldItem && isGolemHurt()) {
            if (!world.isClient()) {
                    if (getHealth() < getMaxHealth()) setHealth(getMaxHealth());
                    playSound(GOLEM_HEAL, 1.0F, 1.0F);
                    playSound(SoundEvents.BLOCK_GRASS_STEP, 1.0F, 1.0F);
                    int newLifespan = Math.min(ConfigHelper.getLifespan() * 2, lifespan.get() + 6000);
                    lifespan.set(newLifespan);
                    if (!player.isCreative()) player.getStackInHand(hand).decrement(1);
                    PacketHandler.sendHealthPacket(this);
            }
            spawnHealParticles(getX(), getY(), getZ());
        } else if (Items.APPLE == heldItem) {
            if (!world.isClient()) {
                int newHunger = Math.min(ConfigHelper.getHunger() * 2, hunger.get() + 12000);
                hunger.set(newHunger);
                if (!player.isCreative()) player.getStackInHand(hand).decrement(1);
                PacketHandler.sendHealthPacket(this);
                playSound(GOLEM_HEAL, 1.0F, 1.0F);
            }
            spawnHappyParticles(getX(), getY(), getZ());
        } else if (Items.AIR == heldItem) {
            if (!world.isClient() && player.isSneaking()) {
                Text message = new TranslatableText("strawgolem.order", getDisplayName().getString());
                player.sendMessage(message, true);
                GolemChestHandler.addMapping(player.getUuid(), getEntityId());
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == DamageSource.SWEET_BERRY_BUSH) return false;
        return super.damage(source, amount);
    }

    /**
     * Returns whether the golem is in an imperfect state (i.e. lifespan is below 90% or it has taken damage)
     * @return whether golem is hurt
     */
    private boolean isGolemHurt() {
        return lifespan.get() < ConfigHelper.getLifespan() * 1.9 || getHealth() < getMaxHealth();
    }

    /**
     * Spawns the heal particles based on location x, y, z
     * @param x coordiante
     * @param y coordinate
     * @param z coordinate
     */
    private void spawnHealParticles(double x, double y, double z) {
        world.addParticle(
                ParticleTypes.HEART,
                x + random.nextDouble() - 0.5, y + 0.4D, z + random.nextDouble() - 0.5,
                this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
    }

    private void spawnHappyParticles(double x, double y, double z) {
        world.addParticle(
                ParticleTypes.HAPPY_VILLAGER,
                x + random.nextDouble() - 0.5, y + 0.4D, z + random.nextDouble() - 0.5,
                this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
    }

    /* Handles being picked up by iron golem */

    @Override
    public void setPos(double posX, double posY, double posZ) {
        if (hasVehicle() && getVehicle() instanceof IronGolemEntity) {
            IronGolemEntity ironGolem = (IronGolemEntity) getVehicle();
            double lookX = ironGolem.getRotationVector().getX();
            double lookZ = ironGolem.getRotationVector().getZ();
            double magnitude = Math.sqrt(lookX * lookX + lookZ * lookZ);
            lookX /= magnitude;
            lookZ /= magnitude;
            super.setPos(posX + 1.85D * lookX, posY - 0.55D, posZ + 1.85D * lookZ);
        } else {
            super.setPos(posX, posY, posZ);
        }
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        if (getVehicle() instanceof IronGolemEntity) {
            LivingEntity ridingEntity = (LivingEntity) getVehicle();
            double lookX = ridingEntity.yaw;
            double lookZ = ridingEntity.pitch;
            double magnitude = Math.sqrt(lookX * lookX + lookZ * lookZ);
            lookX /= magnitude;
            lookZ /= magnitude;
            super.setPos(getPos().x + lookX, getPos().y, getPos().z + lookZ);
        }
    }

    // Harvesting

    /**
     * Determines whether or not the block at position pos in world worldIn should be harvested
     *
     * @param worldIn the world
     * @param pos     the position
     * @return whether the golem should harvest the block
     */
    public boolean shouldHarvestBlock(WorldView worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos);
        if (ConfigHelper.blockHarvestAllowed(state.getBlock())) {
            if (state.getBlock() instanceof CropBlock)
                return ((CropBlock) state.getBlock()).isMature(state);
            else if (state.getBlock() instanceof GourdBlock)
                return true;
            else if (state.getBlock() instanceof NetherWartBlock)
                return state.get(NetherWartBlock.AGE) == 3;
            else if (state.getBlock() instanceof PlantBlock && state.getBlock() instanceof Fertilizable)
                return state.contains(Properties.AGE_3)
                        && state.get(Properties.AGE_3) == 3;
        }
        return false;
    }

    /**
     * Checks if golem has line of sight on the block
     * @param worldIn the world
     * @param pos     the position
     * @return whether the golem has line of sight
     */
    public boolean canSeeBlock(WorldView worldIn, BlockPos pos) {
        Vec3d golemPos = getPos().add(0, 0.75, 0);
        if (getPos().y % 1F != 0) golemPos = golemPos.add(0, 0.5, 0);
        Vec3d blockPos = new Vec3d(pos.getX(), pos.getY() + 0.5, pos.getZ());
        RaycastContext ctx = new RaycastContext(golemPos, blockPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this);
        return worldIn.raycast(ctx).getPos().isInRange(blockPos, 2.5D);
    }

    /* Handles capabilities */

    public SimpleInventory getInventory() {
        return inventory;
    }

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

    /* Harvesting */

    /**
     * Sets the harvest position to pos
     * @param pos new harvest position
     */
    public void setHarvesting(BlockPos pos) {
        harvestPos = pos;
    }

    /**
     * Returns the position to be used to initiate the GolemHarvestGoal
     * @return the harvest position
     */
    public BlockPos getHarvestPos() {
        return harvestPos;
    }

    /**
     * Clears the harvest position
     */
    public void clearHarvestPos() {
        harvestPos = BlockPos.ORIGIN;
    }

    // Storage

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("lifespan", lifespan.writeTag());
        tag.put("hunger", hunger.writeTag());
        tag.put("memory", memory.writeTag());
        tag.put("inventory", inventory.getTags());
        tag.put("tether", tether.writeTag());
        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("lifespan")) lifespan.readTag(tag.get("lifespan"));
        if (tag.contains("hunger")) hunger.readTag(tag.get("hunger"));
        if (tag.contains("memory")) memory.readTag(tag.get("memory"));
        if (tag.contains("inventory")) inventory.readTags((ListTag) tag.get("inventory"));
        if (tag.contains("tether")) tether.readTag(tag.get("tether"));
        super.fromTag(tag);
    }

}
