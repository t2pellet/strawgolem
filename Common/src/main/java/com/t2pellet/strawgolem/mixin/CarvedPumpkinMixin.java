package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.registry.CommonRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinMixin {

    @Shadow
    @Final
    private static Predicate<BlockState> PUMPKINS_PREDICATE;

    private BlockPattern strawGolemBase;
    private BlockPattern strawGolemFull;
    private Block prevBlock;

    @Inject(method = "onPlace", at = @At("HEAD"))
    private void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean $$4, CallbackInfo ci) {
        this.prevBlock = oldState.getBlock();
    }

    @Inject(method = "canSpawnGolem", at = @At("RETURN"), cancellable = true)
    private void canSpawnGolem(LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(ci.getReturnValue() || (StrawgolemConfig.Creation.isDispenserConstructionEnabled() && this.getOrCreateStrawGolemBase().find(world, pos) != null));
    }

    @Inject(method = "trySpawnGolem", at = @At("TAIL"))
    private void trySpawnGolem(Level world, BlockPos pos, CallbackInfo ci) {
        if (prevBlock == Blocks.PUMPKIN && !StrawgolemConfig.Creation.isShearConstructionEnabled()) {
            return;
        }
        BlockPattern.BlockPatternMatch match = this.getOrCreateStrawGolemFull().find(world, pos);
        if (match != null) {
            for (int i = 0; i < this.getOrCreateStrawGolemFull().getHeight(); ++i) {
                BlockInWorld blockInWorld = match.getBlock(0, i, 0);
                world.setBlock(blockInWorld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                world.levelEvent(2001, blockInWorld.getPos(), Block.getId(blockInWorld.getState()));
            }

            StrawGolem golem = CommonRegistry.Entities.getStrawGolemType().create(world);
            BlockPos golemPos = match.getBlock(0, 1, 0).getPos();
            golem.moveTo(golemPos, 0.0F, 0.0F);
            world.addFreshEntity(golem);

            for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0D))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
            }

            for (int i = 0; i < this.getOrCreateStrawGolemFull().getHeight(); ++i) {
                BlockInWorld blockInWorld = match.getBlock(0, i, 0);
                world.blockUpdated(blockInWorld.getPos(), Blocks.AIR);
            }
        }
    }

    private BlockPattern getOrCreateStrawGolemBase() {
        if (this.strawGolemBase == null) {
            this.strawGolemBase = BlockPatternBuilder.start().aisle(" ", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.HAY_BLOCK))).build();
        }
        return this.strawGolemBase;
    }

    private BlockPattern getOrCreateStrawGolemFull() {
        if (this.strawGolemFull == null) {
            this.strawGolemFull = BlockPatternBuilder.start().aisle("^", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.HAY_BLOCK))).build();
        }
        return this.strawGolemFull;
    }

}
