package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;


public class GolemRepairSelfGoal extends MoveToBlockGoal {

    private static final ResourceLocation FEEDING_TROUGH_RESOURCE = new ResourceLocation("animal_feeding_trough", "feeding_trough");

    private final StrawGolem golem;
    private final ServerLevel level;
    private Container feeder;

    public GolemRepairSelfGoal(StrawGolem golem, int range) {
        super(golem, 0.5, range);
        this.golem = golem;
        this.level = (ServerLevel) golem.level;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        BlockEntity blockEntity = levelReader.getBlockEntity(blockPos);
        Block block = levelReader.getBlockState(blockPos).getBlock();
        if (Registry.BLOCK.getKey(block).equals(FEEDING_TROUGH_RESOURCE) && blockEntity instanceof Container container) {
            ItemStack stack = container.getItem(0);
            if (stack.getCount() >= 4 && stack.is(Items.WHEAT)) {
                this.feeder = container;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canUse() {
        return golem.getHeldItem().has() && golem.getDecay().getState() != DecayState.NEW && findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && feeder != null && golem.getDecay().getState() != DecayState.NEW;
    }

    @Override
    public void tick() {
        if (!level.isClientSide && feeder != null && golem.getDecay().getState() != DecayState.NEW) {
            if (feeder.getItem(0).getCount() >= 4) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
            if (isReachedTarget()) {
                feeder.getItem(0).shrink(4);
                golem.getDecay().repair();
            }
        }
        super.tick();
    }

    @Override
    public void start() {
        super.start();
        golem.playSound(StrawgolemSounds.GOLEM_INTERESTED.get());
    }

    @Override
    public double acceptedDistance() {
        return 2.0D;
    }
}
