package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.ai.DeliverGoal;
import com.commodorethrawn.strawgolem.entity.ai.HarvestGoal;
import com.commodorethrawn.strawgolem.entity.capability.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.InventoryProvider;
import com.commodorethrawn.strawgolem.entity.capability.LifespanProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityStrawGolem extends GolemEntity {
	
	public static final ResourceLocation LOOT = new ResourceLocation(Strawgolem.MODID, "strawgolem");

	private ILifespan lifespan;
    public IItemHandler inventory;
	
	public EntityStrawGolem(EntityType<? extends EntityStrawGolem> type, World worldIn) {
		super(type, worldIn);
		inventory = getCapability(InventoryProvider.CROP_SLOT, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		
		if (lifespan == null)
			lifespan = getCapability(LifespanProvider.LIFESPAN_CAP, null).orElseThrow(() -> new IllegalArgumentException("cant be empty"));

		lifespan.update();
		
		if (lifespan.isOver())
			attackEntityFrom(DamageSource.MAGIC, getMaxHealth()*100);
	}

    @Override
	protected void registerGoals() {
        int priority = 0;
        this.goalSelector.addGoal(priority, new SwimGoal(this));
        this.goalSelector.addGoal(++priority, new AvoidEntityGoal<>(this, MonsterEntity.class, 10.0F, 0.6D, 0.7D));
        this.goalSelector.addGoal(++priority, new HarvestGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new DeliverGoal(this, 0.6D));
        this.goalSelector.addGoal(++priority, new LookAtGoal(this, PlayerEntity.class, 5.0F));
        this.goalSelector.addGoal(priority, new WaterAvoidingRandomWalkingGoal(this, 0.5D));
        this.goalSelector.addGoal(++priority, new LookRandomlyGoal(this));
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

    public boolean isHandEmpty() {
        return inventory.getStackInSlot(0).getItem() == Items.AIR;
    }

    @Override
    public ItemStack getHeldItem(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return inventory.getStackInSlot(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        ArrayList<ItemStack> heldEquipment = new ArrayList<>();
        heldEquipment.add(inventory.getStackInSlot(0));
        return heldEquipment;
    }

    @Override
    @Nullable
	protected SoundEvent getAmbientSound() {
		return null; // TODO
	}

	@Override @Nullable
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return null; // TODO
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return null; // TODO
	}

	@Override
	public int getTalkInterval() {
		return 120;
	}

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return false;
	}

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return false;
    }

    @Override
	protected ResourceLocation getLootTable() {
		return LOOT;
	}
	
}
