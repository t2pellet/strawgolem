package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.ILifespan;
import com.commodorethrawn.strawgolem.entity.capability.lifespan.LifespanProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityStrawGolem extends GolemEntity {
	
	public static final ResourceLocation LOOT = new ResourceLocation(Strawgolem.MODID, "strawgolem");

	private ILifespan lifespan;
	
	public EntityStrawGolem(EntityType<? extends EntityStrawGolem> type, World worldIn) {
		super(type, worldIn);
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
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, MonsterEntity.class, 10.0F, 0.6D, 0.7D));
		this.goalSelector.addGoal(2, new HarvestGoal(this, 0.6D));
		this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 5.0F));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
		this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override @Nullable
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
		return 120; // TODO
	}

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return false;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LOOT;
	}
	
}
