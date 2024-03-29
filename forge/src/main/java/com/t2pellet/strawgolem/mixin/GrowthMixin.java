package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.events.CropGrowthEvent;
import com.t2pellet.strawgolem.util.crop.CropUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class GrowthMixin {

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", at = @At("TAIL"))
    public void grow(BlockPos pos, BlockState newState, int i, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof ServerLevel level) {
            BlockPos cropPos = pos;
            Block block = newState.getBlock();
            if (block instanceof AttachedStemBlock) {
                cropPos = pos.offset(newState.getValue(AttachedStemBlock.FACING).getNormal());
            }
            if (CropUtil.isGrownCrop(level, pos)) {
                Constants.LOG.debug("Crop grown in world: {}, at pos: {}", level.toString(), pos.toShortString());
                BlockPos finalCropPos = cropPos;
                level.getServer().execute(() -> CropGrowthEvent.onCropGrowth(level, finalCropPos, newState));
            }
        }
    }
}
