package com.commodorethrawn.strawgolem.entity.strawgolem;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.ai.*;
import com.commodorethrawn.strawgolem.entity.capability.InventoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanProvider;
import com.commodorethrawn.strawgolem.entity.capability.memory.IMemory;
import com.commodorethrawn.strawgolem.entity.capability.memory.MemoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.profession.IProfession;
import com.commodorethrawn.strawgolem.entity.capability.profession.ProfessionProvider;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.items.IItemHandler;

import java.util.Random;

public class EntityStrawGolem extends GolemEntity {
    public static final SoundEvent GOLEM_AMBIENT = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_ambient"));
    public static final SoundEvent GOLEM_STRAINED = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_strained"));
    public static final SoundEvent GOLEM_HURT = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_hurt"));
    public static final SoundEvent GOLEM_DEATH = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_death"));
    public static final SoundEvent GOLEM_HEAL = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_heal"));
    public static final SoundEvent GOLEM_SCARED = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_scared"));
    public static final SoundEvent GOLEM_INTERESTED = new SoundEvent(new ResourceLocation(Strawgolem.MODID, "golem_interested"));
    private static final ResourceLocation LOOT = new ResourceLocation(Strawgolem.MODID, "strawgolem");
    public IItemHandler inventory;
    private ILifespan lifespan;
    private IMemory memory;
    private IProfession profession;
    private BlockPos harvestPos;

    public EntityStrawGolem(EntityType<? extends EntityStrawGolem> type, World worldIn) {
        super(type, worldIn);
        inventory = getCapability(InventoryProvider.CROP_SLOT, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));
        profession = getCapability(ProfessionProvider.PROFESSION_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));
        lifespan = getCapability(LifespanProvider.LIFESPAN_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));
        harvestPos = BlockPos.ZERO;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (StrawgolemConfig.isSoundsEnabled()) {
            if (goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof GolemFleeGoal))
                return GOLEM_SCARED;
            else if (holdingFullBlock()) return GOLEM_STRAINED;
            return GOLEM_AMBIENT;
        }
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return StrawgolemConfig.isSoundsEnabled() ? GOLEM_HURT : null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return StrawgolemConfig.isSoundsEnabled() ? GOLEM_DEATH : null;
    }

    @Override
    public int getTalkInterval() {
        return holdingFullBlock() ? 60 : 120;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected void registerGoals() {
        int priority = 0;
        this.goalSelector.addGoal(priority, new SwimGoal(this));
        this.goalSelector.addGoal(++priority, new GolemFleeGoal(this));
        this.goalSelector.addGoal(++priority, new GolemTemptGoal(this));
        this.goalSelector.addGoal(++priority, new GolemHarvestGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new GolemDeliverGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new GolemWanderGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new GolemLookAtPlayerGoal(this, 5.0F));
        this.goalSelector.addGoal(++priority, new GolemLookRandomlyGoal(this));
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (memory == null)
            memory = getCapability(MemoryProvider.MEMORY_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));

        lifespan.update();
        if (holdingFullBlock()) lifespan.update();
        if (world.isRainingAt(getPosition()) && world.canSeeSky(getPosition())) lifespan.update();

        if (lifespan.isOver())
            attackEntityFrom(DamageSource.MAGIC, getMaxHealth() * 100);


    }

    /* Handle inventory */

    /**
     * Returns true if the golem is not holding anything, and false otherwise
     *
     * @return whether the hand is empty
     */
    public boolean isHandEmpty() {
        return getHeldItemMainhand().isEmpty();
    }

    @Override
    public ItemStack getHeldItem(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return inventory.getStackInSlot(0);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns true if the golem is holding a block crop, and false otherwise
     *
     * @return whether the golem is holding a gourd block
     */
    public boolean holdingFullBlock() {
        return Block.getBlockFromItem(inventory.getStackInSlot(0).getItem()) instanceof StemGrownBlock;
    }

    /* Handle picking up items */

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    @Override
    public boolean canPickUpItem(ItemStack itemstackIn) {
        if (isHandEmpty() || itemstackIn.getItem() == getHeldItemMainhand().getItem()) {
            return (itemstackIn.getUseAction() == UseAction.EAT
                    || itemstackIn.getItem() == Items.NETHER_WART
                    || !(itemstackIn.getItem() instanceof BlockItem))
                    && itemstackIn.getCount() + getHeldItemMainhand().getCount() < getHeldItemMainhand().getMaxStackSize();
        }
        return false;
    }

    @Override
    public void onItemPickup(Entity entityIn, int quantity) {
        if (entityIn instanceof ItemEntity) {
            ItemEntity item = (ItemEntity) entityIn;
            ItemStack stack = item.getItem();
            inventory.insertItem(0, stack, false);
        }
        super.onItemPickup(entityIn, quantity);
    }

    /* Miscellaneous */

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        if (EffectiveSide.get().isServer()) {
            entityDropItem(inventory.getStackInSlot(0).copy());
            inventory.getStackInSlot(0).setCount(0);
        }
    }

    // Handle setting priority chest & healing
    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if (player.getHeldItem(hand).getItem() == Items.WHEAT) {
            spawnHealParticles(lastTickPosX, lastTickPosY, lastTickPosZ);
            if (!player.isShiftKeyDown() &&
                    (getHealth() != getMaxHealth() || getCurrentLifespan() < StrawgolemConfig.getLifespan() * 2)) {
                if (EffectiveSide.get().isServer()) {
                    setHealth(getMaxHealth());
                    playSound(GOLEM_HEAL, 1.0F, 1.0F);
                    playSound(SoundEvents.BLOCK_GRASS_STEP, 1.0F, 1.0F);
                    addToLifespan(14000);
                    if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                }
            } else {
                if (EffectiveSide.get().isServer())
                    player.sendMessage(new StringTextComponent("Ordering: ").appendSibling(getDisplayName()));
                player.getPersistentData().putInt("golemId", getEntityId());
            }
        }
        return false;
    }

    /**
     * Spawns the heal particles based on location x, y, z
     *
     * @param x
     * @param y
     * @param z
     */
    private void spawnHealParticles(double x, double y, double z) {
        Random rand = new Random();
        world.addParticle(ParticleTypes.HEART, x + rand.nextDouble() - 0.5, y + 0.4D, z + rand.nextDouble() - 0.5, this.getMotion().x, this.getMotion().y, this.getMotion().z);
    }

    /* Handles being picked up by iron golem */

    @Override
    public void setPosition(double posX, double posY, double posZ) {
        if (isPassenger() && getRidingEntity() instanceof IronGolemEntity) {
            IronGolemEntity ironGolem = (IronGolemEntity) getRidingEntity();
            double lookX = ironGolem.getLookVec().x;
            double lookZ = ironGolem.getLookVec().z;
            double magnitude = Math.sqrt(lookX * lookX + lookZ * lookZ);
            lookX /= magnitude;
            lookZ /= magnitude;
            super.setPosition(posX + 1.9D * lookX, posY - 0.5D, posZ + 1.9D * lookZ);
        } else {
            super.setPosition(posX, posY, posZ);
        }
    }

    @Override
    public void stopRiding() {
        LivingEntity ridingEntity = (LivingEntity) getRidingEntity();
        super.stopRiding();
        if (ridingEntity instanceof IronGolemEntity) {
            double lookX = ridingEntity.getLookVec().x;
            double lookZ = ridingEntity.getLookVec().z;
            double magnitude = Math.sqrt(lookX * lookX + lookZ * lookZ);
            lookX /= magnitude;
            lookZ /= magnitude;
            super.setPosition(getPositionVec().x + lookX, getPositionVec().y, getPositionVec().z + lookZ);
        }
    }

    /* Helps with harvesting goal */

    public boolean shouldHarvestBlock(IWorldReader worldIn, BlockPos pos) {
        Vec3d posVec = getPositionVec();
        if (posVec.getY() % 1 > 0.01) posVec = posVec.add(0, 1, 0);
        RayTraceContext ctx = new RayTraceContext(posVec, new Vec3d(pos), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this);
        if (!worldIn.rayTraceBlocks(ctx).getPos().equals(pos)) return false;
        BlockState block = worldIn.getBlockState(pos);
        if (StrawgolemConfig.blockHarvestAllowed(block.getBlock())) {
            if (block.getBlock() instanceof CropsBlock)
                return ((CropsBlock) block.getBlock()).isMaxAge(block);
            else if (block.getBlock() instanceof StemGrownBlock)
                return true;
            else if (block.getBlock() == Blocks.NETHER_WART)
                return block.get(NetherWartBlock.AGE) == 3;
            else if (block.getBlock() instanceof BushBlock && block.getBlock() instanceof IGrowable)
                return block.has(BlockStateProperties.AGE_0_3) && block.get(BlockStateProperties.AGE_0_3) == 3;
        }
        return false;
    }

    /* Handles capabilities */

    /**
     * Returnns the memory, capability, used to store and retrieve chest positions
     *
     * @return the golem's memory capability
     */
    public IMemory getMemory() {
        return memory;
    }

    /**
     * Sets the harvest position to pos
     *
     * @param pos
     */
    public void setHarvesting(BlockPos pos) {
        harvestPos = pos;
    }

    /**
     * Returns the position to be used to initiate the GolemHarvestGoal
     *
     * @return the harvest position
     */
    public BlockPos getHarvestPos() {
        return harvestPos;
    }

    /**
     * Clears the harvest position
     */
    public void clearHarvestPos() {
        harvestPos = BlockPos.ZERO;
    }

    public int getCurrentLifespan() {
        return lifespan.get();
    }

    /**
     * Adds time to the lifespan
     *
     * @param time
     */
    public void addToLifespan(int time) {
        lifespan.set(lifespan.get() + time);
    }
}
