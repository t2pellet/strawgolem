package com.t2pellet.strawgolem.entity.goals;

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


public class ReturnToTetherGoal extends MoveToBlockGoal {

    private final StrawGolem golem;
    private final ServerLevel level;

    public ReturnToTetherGoal(StrawGolem golem) {
        super(golem, 0.5, 24);
        this.golem = golem;
        this.level = (ServerLevel) golem.level;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return golem.getTether().get().equals(blockPos);
    }

    @Override
    public boolean canUse() {
        return !golem.getHeldItem().has() && findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return !golem.getHeldItem().has() && isValidTarget(level, blockPos);
    }

    @Override
    protected boolean findNearestBlock() {
        blockPos = golem.getTether().get();
        return golem.getTether().isTooFar();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
            if (isReachedTarget()) {
                stop();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        golem.playSound(StrawgolemSounds.GOLEM_INTERESTED);
    }

    @Override
    public double acceptedDistance() {
        return golem.getRandom().nextInt(8);
    }
}
