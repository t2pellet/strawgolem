package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.crop.CropRegistry;
import com.t2pellet.strawgolem.events.CropGrowthCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
            if (newState.getBlock() instanceof AttachedStemBlock) {
                cropPos = pos.offset(newState.getValue(AttachedStemBlock.FACING).getNormal());
            }
            BlockEntity entity = world.getBlockEntity(cropPos);
            if (CropRegistry.INSTANCE.isGrownCrop(newState) || CropRegistry.INSTANCE.isGrownCrop(entity)) {
                CropGrowthCallback.EVENT.invoker().grow(world, cropPos);
            }
        }
    }
}
