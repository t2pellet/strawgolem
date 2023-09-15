package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.entity.EntityTypes;
import com.t2pellet.strawgolem.entity.StrawGolem;
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
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinMixin {

    private BlockPattern strawGolemBase;
    private BlockPattern strawGolemFull;

    // I don't feel like doing an access transformer, so I'm copying this
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = (p_51396_) -> {
        return p_51396_ != null && (p_51396_.is(Blocks.CARVED_PUMPKIN) || p_51396_.is(Blocks.JACK_O_LANTERN));
    };

    @Inject(method = "canSpawnGolem", at = @At("RETURN"))
    public void canSpawnGolem(LevelReader levelReader, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        boolean canSpawn = ci.getReturnValue() || getOrCreateStrawGolemBase().find(levelReader, pos) != null;
        ci.setReturnValue(canSpawn);
    }

    @Inject(method = "trySpawnGolem", at = @At("TAIL"))
    private void trySpawnGolem(Level level, BlockPos pos, CallbackInfo ci) {
        BlockPattern.BlockPatternMatch patternMatch = this.getOrCreateStrawGolemFull().find(level, pos);
        if (patternMatch != null) {
            // Clear it
            for (int i = 0; i < this.getOrCreateStrawGolemFull().getHeight(); ++i) {
                BlockInWorld blockinworld = patternMatch.getBlock(0, i, 0);
                level.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                level.levelEvent(2001, blockinworld.getPos(), Block.getId(blockinworld.getState()));
            }

            // Spawn it
            StrawGolem strawGolem = EntityTypes.strawGolem().create(level);
            BlockPos blockPos = patternMatch.getBlock(0, 1, 0).getPos();
            strawGolem.moveTo(Vec3.atCenterOf(blockPos));
            level.addFreshEntity(strawGolem);

            // Summon trigger
            for(ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, strawGolem.getBoundingBox().inflate(5.0D))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, strawGolem);
            }

            // Block update
            for(int l = 0; l < this.getOrCreateStrawGolemFull().getHeight(); ++l) {
                BlockInWorld blockinworld3 = patternMatch.getBlock(0, l, 0);
                level.blockUpdated(blockinworld3.getPos(), Blocks.AIR);
            }
        }
    }

    private BlockPattern getOrCreateStrawGolemBase() {
        if (strawGolemBase == null) {
            strawGolemBase = BlockPatternBuilder.start().aisle(" ", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.HAY_BLOCK))).build();
        }
        return strawGolemBase;
    }

    private BlockPattern getOrCreateStrawGolemFull() {
        if (strawGolemFull == null) {
            strawGolemFull = BlockPatternBuilder.start().aisle("^", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.HAY_BLOCK))).build();
        }
        return strawGolemFull;
    }

}
