package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.ai.GolemDeliverGoal;
import com.commodorethrawn.strawgolem.entity.ai.GolemHarvestGoal;
import com.commodorethrawn.strawgolem.entity.ai.GolemWanderGoal;
import com.commodorethrawn.strawgolem.entity.capability.InventoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanProvider;
import com.commodorethrawn.strawgolem.entity.capability.memory.IMemory;
import com.commodorethrawn.strawgolem.entity.capability.memory.MemoryProvider;
import net.minecraft.block.Block;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class EntityStrawGolem extends GolemEntity {
	
	public static final ResourceLocation LOOT = new ResourceLocation(Strawgolem.MODID, "strawgolem");
    public IItemHandler inventory;
    private ILifespan lifespan;
    private IMemory memory;

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null; // TODO
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null; // TODO
    }

    @Override
    public int getTalkInterval() {
        return 120;
    }

    public EntityStrawGolem(EntityType<? extends EntityStrawGolem> type, World worldIn) {
		super(type, worldIn);
		inventory = getCapability(InventoryProvider.CROP_SLOT, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));
	}

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected void registerGoals() {
        int priority = 0;
        this.goalSelector.addGoal(priority, new SwimGoal(this));
        this.goalSelector.addGoal(++priority, new AvoidEntityGoal<>(this, MonsterEntity.class, 10.0F, 0.6D, 0.75D));
        this.goalSelector.addGoal(++priority, new GolemHarvestGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new GolemDeliverGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new GolemWanderGoal(this, 0.5D));
        this.goalSelector.addGoal(++priority, new LookAtGoal(this, PlayerEntity.class, 5.0F));
        this.goalSelector.addGoal(++priority, new LookRandomlyGoal(this));
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (memory == null)
            memory = getCapability(MemoryProvider.MEMORY_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));

        if (lifespan == null)
            lifespan = getCapability(LifespanProvider.LIFESPAN_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));

        lifespan.update();
        if (holdingBlockCrop()) lifespan.update();

        if (lifespan.isOver())
            attackEntityFrom(DamageSource.MAGIC, getMaxHealth() * 100);
    }

    @Override
    protected void onDeathUpdate() {
        ItemEntity heldItem = new ItemEntity(this.world, this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ, this.getHeldItem(Hand.MAIN_HAND));
        this.getEntityWorld().addEntity(heldItem);
        super.onDeathUpdate();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

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

    public boolean holdingBlockCrop() {
        return Block.getBlockFromItem(inventory.getStackInSlot(0).getItem()) instanceof StemGrownBlock;
    }

    public void addChestPos(BlockPos pos) {
        if (!memory.containsPosition(pos))
            memory.addPosition(pos);
    }

    public BlockPos getChestPos() {
        return memory.getClosestPosition(this.getPosition());
    }

    public void removeChestPos(BlockPos pos) {
        memory.removePosition(pos);
    }
}
