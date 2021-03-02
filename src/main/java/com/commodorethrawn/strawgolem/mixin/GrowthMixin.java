package com.commodorethrawn.strawgolem.mixin;

import com.commodorethrawn.strawgolem.events.CropGrowthCallback;
import net.minecraft.block.*;
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
		Block block = state.getBlock();
		if ((block instanceof Fertilizable && block instanceof PlantBlock) || block instanceof AttachedStemBlock) {
			CropGrowthCallback.EVENT.invoker().grow((World) (Object) this, pos);
		}
	}
}
