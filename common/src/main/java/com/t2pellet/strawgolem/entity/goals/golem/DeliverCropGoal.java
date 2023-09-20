package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.StrawgolemSounds;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import com.t2pellet.strawgolem.util.crop.CropUtil;
import com.t2pellet.strawgolem.world.WorldCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;


public class DeliverCropGoal extends MoveToBlockGoal {

    private final StrawGolem golem;
    private final ServerLevel level;

    public DeliverCropGoal(StrawGolem golem) {
        super(golem, 0.5, StrawgolemConfig.Harvesting.harvestRange.get());
        this.golem = golem;
        this.level = (ServerLevel) golem.level;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return ContainerUtil.isContainer((LevelAccessor) levelReader, blockPos);
    }

    @Override
    public boolean canUse() {
        return golem.getHeldItem().has() && findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return golem.getHeldItem().has() && isValidTarget(golem.level, blockPos);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
            if (isReachedTarget() && isValidTarget(level, blockPos)) {
                golem.getDeliverer().deliver(blockPos);
            }
        }
    }

    @Override
    public void start() {
        super.start();
        if (golem.isHoldingBlock()) golem.playSound(StrawgolemSounds.GOLEM_STRAINED);
        else golem.playSound(StrawgolemSounds.GOLEM_INTERESTED);
    }

    @Override
    public double acceptedDistance() {
        return 1.6D;
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos blockPos = golem.getDeliverer().getDeliverPos();
        if (isValidTarget(mob.getLevel(), blockPos)) {
            this.blockPos = blockPos;
            return true;
        }
        return false;
    }
}
