package com.commodorethrawn.strawgolem.mixin;

import com.commodorethrawn.strawgolem.crop.CropValidator;
import com.commodorethrawn.strawgolem.events.CropGrowthCallback;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class GrowthMixin {

	@Inject(method = "setBlockState", at = @At("TAIL"))
	public void grow(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> info) {
		World world = (World) (Object) this;
		BlockPos cropPos = pos;
		Block block = state.getBlock();
		if (CropValidator.isStem(block)) {
			cropPos = pos.add(state.get(AttachedStemBlock.FACING).getVector());
			block = world.getBlockState(cropPos).getBlock();
		}
		if (CropValidator.isCrop(block) || CropValidator.isCrop(world.getBlockEntity(pos))) {
			CropGrowthCallback.EVENT.invoker().grow(world, cropPos);
		}
	}
}
