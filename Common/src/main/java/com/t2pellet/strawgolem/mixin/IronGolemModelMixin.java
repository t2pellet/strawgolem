package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemModel.class)
public class IronGolemModelMixin {

    @Shadow
    @Final
    private ModelPart arm0;
    @Shadow
    @Final
    private ModelPart arm1;

    @Inject(method = "prepareMobModel", at = @At("HEAD"), cancellable = true)
    private void prepareMobModel(IronGolem entity, float f, float g, float h, CallbackInfo ci) {
        int i = entity.getAttackAnimationTick();
        if (i > 0) {
            this.arm0.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - h, 10.0F);
            this.arm1.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - h, 10.0F);
        } else {
            int j = entity.getOfferFlowerTick();
            if (j > 0) {
                this.arm0.xRot = -0.8F + 0.025F * Mth.triangleWave((float) j, 70.0F);
                this.arm1.xRot = 0.0F;
            } else if (entity.hasPassenger(StrawGolem.class) && entity.getPassengers().size() == 1) {
                arm1.xRot = -0.45F * (float) Math.PI;
                arm0.xRot = -0.45F * (float) Math.PI;
                arm1.yRot = 0.18F;
                arm0.yRot = -0.18F;
            } else {
                this.arm0.xRot = (-0.2F + 1.5F * Mth.triangleWave(f, 13.0F)) * g;
                this.arm1.xRot = (-0.2F - 1.5F * Mth.triangleWave(f, 13.0F)) * g;
            }
        }
        ci.cancel();
    }

}
