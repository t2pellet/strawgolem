package com.commodorethrawn.strawgolem.mixin;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public class LoginMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void sendPacket(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        List<EntityStrawGolem> golems = player.world.getEntitiesByClass(EntityStrawGolem.class, player.getBoundingBox().expand(25), e -> true);
        for (EntityStrawGolem golem : golems) {
            PacketHandler.sendLifespanPacket(golem);
            PacketHandler.sendHoldingPacket(golem);
        }
    }



}
