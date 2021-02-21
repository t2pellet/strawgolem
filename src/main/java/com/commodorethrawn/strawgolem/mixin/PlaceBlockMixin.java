package com.commodorethrawn.strawgolem.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class PlaceBlockMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z"),
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            cancellable = true)
    private void onPlaced(ItemPlacementContext ctx, CallbackInfoReturnable<Boolean> info) {
        ActionResult result = PlaceBlockCallback.EVENT.invoker().interact(ctx.getWorld(), ctx.getBlockPos());
        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
