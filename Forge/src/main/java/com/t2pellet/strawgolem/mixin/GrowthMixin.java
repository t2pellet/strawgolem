package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.crop.CropRegistry;
import com.t2pellet.strawgolem.events.StrawGolemEvents;
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

    @Inject(method = "setBlock", at = @At("TAIL"))
    public void grow(BlockPos pos, BlockState newState, int i, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof ServerLevel world) {
            BlockPos cropPos = pos;
            Block block = newState.getBlock();
            if (block instanceof AttachedStemBlock) {
                cropPos = pos.offset(newState.getValue(AttachedStemBlock.FACING).getNormal());
            }
            if (CropRegistry.INSTANCE.isGrownCrop(newState) || CropRegistry.INSTANCE.isGrownCrop(world.getBlockEntity(cropPos))) {
                StrawgolemCommon.LOG.debug("Crop grown in world: {}, at pos: {}", world.toString(), pos.toShortString());
                StrawGolemEvents.onCropGrowth(world, cropPos, newState);
            }
        }
    }
}
