package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.crop.CropValidator;
import com.t2pellet.strawgolem.events.CropGrowthCallback;
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
			if (CropValidator.isStem(block)) {
				cropPos = pos.offset(newState.getValue(AttachedStemBlock.FACING).getNormal());
			}
			if (CropValidator.isGrownCrop(newState) || CropValidator.isGrownCrop(world.getBlockEntity(pos))) {
				CropGrowthCallback.EVENT.invoker().grow(world, cropPos);
			}
		}
	}
}
