package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.StrawgolemFabric;
import com.t2pellet.strawgolem.events.WorldSaveCallback;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "saveLevelData", at = @At("RETURN"))
    private void saveLevelData(CallbackInfo ci) {
        StrawgolemFabric.getServer().execute(() -> WorldSaveCallback.EVENT.invoker().save((ServerLevel) (Object) this));
    }
}
