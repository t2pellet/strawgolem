package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.strawgolem.util.crop.CropUtil;
import com.t2pellet.strawgolem.world.WorldCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.phys.Vec3;


public class HarvestCropGoal extends MoveToBlockGoal {

    private final StrawGolem golem;
    private final ServerLevel level;

    public HarvestCropGoal(StrawGolem golem) {
        super(golem, 0.5, StrawgolemConfig.Harvesting.harvestRange.get());
        this.golem = golem;
        this.level = (ServerLevel) golem.level;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return CropUtil.isGrownCrop((LevelAccessor) levelReader, blockPos);
    }

    @Override
    public boolean canUse() {
        return !golem.getHeldItem().has() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !golem.getHeldItem().has() && super.canContinueToUse();
    }

    @Override
    protected int nextStartTick(PathfinderMob $$0) {
        return reducedTickDelay(100 + $$0.getRandom().nextInt(100));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (isReachedTarget() && isValidTarget(level, blockPos)) {
                golem.getHarvester().harvest(blockPos);
            }
        }
        if (!golem.getLookControl().isLookingAtTarget()) {
            golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
        }
    }

    @Override
    public void start() {
        super.start();
        golem.playSound(StrawgolemSounds.GOLEM_INTERESTED.get());
        // Update the tether to the crop we're harvesting
        golem.getTether().update(blockPos);
    }

    @Override
    public double acceptedDistance() {
        if (level.getBlockState(blockPos).getBlock() instanceof StemGrownBlock) {
            return 2.6D;
        }
        return 1.6D;
    }

    @Override
    protected boolean findNearestBlock() {
        WorldCrops crops = WorldCrops.of((ServerLevel) mob.getLevel());
        BlockPos blockPos = crops.findNearest(mob.blockPosition());
        if (isValidTarget(mob.getLevel(), blockPos)) {
            this.blockPos = blockPos;
            return true;
        }
        crops.remove(blockPos);
        return false;
    }
}
