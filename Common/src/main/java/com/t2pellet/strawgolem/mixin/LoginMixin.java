package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.entity.ai.GolemTemptGoal;
import com.t2pellet.strawgolem.network.GreedyPacket;
import com.t2pellet.strawgolem.network.HealthPacket;
import com.t2pellet.strawgolem.network.HoldingPacket;
import com.t2pellet.strawgolem.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
public class LoginMixin {

    @Inject(method = "addDuringCommandTeleport", at = @At("TAIL"))
    public void playerConnnected(ServerPlayer player, CallbackInfo info) {
        sendPacket(player);
    }

    @Inject(method = "addDuringPortalTeleport", at = @At("TAIL"))
    public void playerRespawned(ServerPlayer player, CallbackInfo info) {
        sendPacket(player);
    }

    @Inject(method = "addNewPlayer", at = @At("TAIL"))
    public void playerDimChange(ServerPlayer Player, CallbackInfo info) {
        sendPacket(Player);
    }

    @Inject(method = "addRespawnedPlayer", at = @At("TAIL"))
    public void playerTeleport(ServerPlayer Player, CallbackInfo info) {
        sendPacket(Player);
    }

    public void sendPacket(ServerPlayer player) {
        List<EntityStrawGolem> golems = player.level.getEntitiesOfClass(EntityStrawGolem.class, player.getBoundingBox().inflate(30), e -> true);
        for (EntityStrawGolem golem : golems) {
            Services.PACKETS.sendTo(new HealthPacket(golem), player);
            Services.PACKETS.sendTo(new HoldingPacket(golem), player);
            boolean greedy = golem.isRunningGoal(GolemTemptGoal.class);
            Services.PACKETS.sendTo(new GreedyPacket(golem, greedy), player);
        }
    }

}
