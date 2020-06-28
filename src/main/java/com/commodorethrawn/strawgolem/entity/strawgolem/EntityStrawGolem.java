package com.commodorethrawn.strawgolem.entity.strawgolem;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.ai.*;
import com.commodorethrawn.strawgolem.entity.capability.InventoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanProvider;
import com.commodorethrawn.strawgolem.entity.capability.memory.IMemory;
import com.commodorethrawn.strawgolem.entity.capability.memory.MemoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.profession.IProfession;
import com.commodorethrawn.strawgolem.entity.capability.profession.ProfessionProvider;
import net.minecraft.block.Block;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityStrawGolem extends GolemEntity {
	
	private static final ResourceLocation LOOT = new ResourceLocation(Strawgolem.MODID, "strawgolem");
    public IItemHandler inventory;
    private ILifespan lifespan;
    private IMemory memory;
    private IProfession profession;
    private BlockPos harvestPos;

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
        profession = getCapability(ProfessionProvider.PROFESSION_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));
        harvestPos = BlockPos.ZERO;
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
        this.goalSelector.addGoal(++priority, new TemptGoal(this, 0.7D, false, Ingredient.fromItems(Items.APPLE)));
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

        if (lifespan == null)
            lifespan = getCapability(LifespanProvider.LIFESPAN_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));

        lifespan.update();
        if (holdingBlockCrop()) lifespan.update();

        if (lifespan.isOver())
            attackEntityFrom(DamageSource.MAGIC, getMaxHealth() * 100);
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

    /* Makes golem drop held item */
    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        if (EffectiveSide.get().isServer()) {
            entityDropItem(inventory.getStackInSlot(0).copy());
            inventory.getStackInSlot(0).setCount(0);
        }
    }

    /* Handle setting priority chest & healing */
    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        if (player.getHeldItem(hand).getItem() == Items.WHEAT) {
            spawnHealParticles(lastTickPosX, lastTickPosY, lastTickPosZ);
            if (!player.isShiftKeyDown()) {
                setHealth(getMaxHealth());
                if (EffectiveSide.get().isServer()) {
                    addToLifespan(14000);
                    player.getHeldItem(hand).shrink(1);
                }
            } else {
                if (EffectiveSide.get().isServer())
                    player.sendMessage(new StringTextComponent("Ordering: ").appendSibling(getDisplayName()));
                player.getPersistentData().putInt("golemId", getEntityId());
            }
        }
        return false;
    }

    private void spawnHealParticles(double x, double y, double z) {
        Random rand = new Random();
        world.addParticle(ParticleTypes.HEART, x + rand.nextDouble() - 0.5, y + 0.4D, z + rand.nextDouble() - 0.5, this.getMotion().x, this.getMotion().y, this.getMotion().z);
    }

    /* Used to handle when the iron golem picks it up */
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

    public IMemory getMemory() {
        return memory;
    }

    public void setHarvesting(BlockPos pos) {
        harvestPos = pos;
    }

    public BlockPos getHarvestPos() {
        return harvestPos;
    }

    public void clearHarvestPos() {
        harvestPos = BlockPos.ZERO;
    }

    public void addToLifespan(int time) {
        lifespan.set(lifespan.get() + time);
    }
}
