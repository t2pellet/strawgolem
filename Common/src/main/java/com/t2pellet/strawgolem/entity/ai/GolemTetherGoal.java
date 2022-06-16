package com.t2pellet.strawgolem.entity.ai;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.capability.hunger.IHasHunger;
import com.t2pellet.strawgolem.entity.capability.tether.IHasTether;
import com.t2pellet.strawgolem.entity.capability.tether.Tether;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import static com.t2pellet.strawgolem.registry.CommonRegistry.Sounds.GOLEM_SCARED;

public class GolemTetherGoal<T extends PathfinderMob & IHasTether> extends MoveToBlockGoal {

    private final T entity;
    private final int desiredDistance;

    public GolemTetherGoal(T entity, double speed) {
        super(entity, speed, StrawgolemConfig.Harvest.getSearchRange(), StrawgolemConfig.Harvest.getSearchRange());
        this.entity = entity;
        desiredDistance = entity.getRandom().nextInt(StrawgolemConfig.Tether.getTetherMaxRange()) + StrawgolemConfig.Tether.getTetherMinRange();
    }

    @Override
    public boolean canUse() {
        if (entity instanceof IHasHunger hungerHaver && hungerHaver.getHunger().isHungry()) {
            return false;
        }
        final double currentDistance = getTetherDistance();
        if (currentDistance > StrawgolemConfig.Tether.getTetherMaxRange()) {
            this.blockPos = entity.getTether().get().getPos();
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (entity instanceof IHasHunger hungerHaver && hungerHaver.getHunger().isHungry()) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        if (StrawgolemConfig.Miscellaneous.isSoundsEnabled()) {
            entity.playSound(GOLEM_SCARED, 1.0F, 1.0F);
        }
        super.start();
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() != Blocks.AIR;
    }

    @Override
    public void tick() {
        this.entity.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
        if (!this.blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                double speed = this.speedModifier;
                this.mob.getNavigation().moveTo(
                        this.blockPos.getX() + 0.5D,
                        this.blockPos.getY() + 1D,
                        this.blockPos.getZ() + 0.5D,
                        speed);
            }
        } else {
            tryTicks = 0;
        }
    }

    @Override
    public double acceptedDistance() {
        return desiredDistance;
    }

    private double getTetherDistance() {
        // Set tether if unset
        if (entity.getTether().get() == Tether.TetherPos.ORIGIN) {
            // if anchor is unset, this is a new golem, set it
            StrawgolemCommon.LOG.debug("Golem {} has no anchor, setting to: {}", entity.getId(), entity.blockPosition());
            entity.getTether().set(entity.level, entity.blockPosition());
            return 0.0;
        }
        return entity.getTether().distanceTo(entity);
    }

}
