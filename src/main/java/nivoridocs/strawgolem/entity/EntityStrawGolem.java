package nivoridocs.strawgolem.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityStrawGolem extends EntityGolem {
	
	public EntityStrawGolem(World worldIn) {
		super(worldIn);
		this.height /= 2.0f;
	}
	
	@Override
	protected void initEntityAI() {
		int i = 0;
		this.tasks.addTask(i++, new EntityAISwimming(this));
		this.tasks.addTask(i++, new EntityAIAvoidEntity(this, EntityMob.class, 8.0F, 0.6D, 0.6D));
		this.tasks.addTask(i++, new EntityAIHarvest(this, 0.6D));
		this.tasks.addTask(i++, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(i, new EntityAILookIdle(this));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {

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
	protected boolean canDespawn() {
		return false;
	}

}
