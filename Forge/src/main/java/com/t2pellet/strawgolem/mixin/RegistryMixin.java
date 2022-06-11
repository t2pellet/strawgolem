package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.events.StrawGolemEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public class RegistryMixin {

    @Inject(method = "register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("RETURN"))
    private static <V, T extends V> void register(Registry<V> registry, ResourceLocation key, T object, CallbackInfoReturnable<T> ci) {
        if (object instanceof Block) {
            StrawGolemEvents.onBlockRegistered((Block) object);
        }
    }
}
