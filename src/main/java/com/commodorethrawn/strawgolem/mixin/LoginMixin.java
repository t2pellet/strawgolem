package com.commodorethrawn.strawgolem.mixin;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.ai.GolemTemptGoal;
import com.commodorethrawn.strawgolem.network.GreedyPacket;
import com.commodorethrawn.strawgolem.network.HealthPacket;
import com.commodorethrawn.strawgolem.network.HoldingPacket;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public class LoginMixin {

    @Inject(method = "onPlayerConnected", at = @At("TAIL"))
    public void playerConnnected(ServerPlayerEntity player, CallbackInfo info) {
        sendPacket(player);
    }

    @Inject(method = "onPlayerRespawned", at = @At("TAIL"))
    public void playerRespawned(ServerPlayerEntity player, CallbackInfo info) {
        sendPacket(player);
    }

    @Inject(method = "onPlayerChangeDimension", at = @At("TAIL"))
    public void playerDimChange(ServerPlayerEntity playerEntity, CallbackInfo info) {
        sendPacket(playerEntity);
    }

    @Inject(method = "onPlayerTeleport", at = @At("TAIL"))
    public void playerTeleport(ServerPlayerEntity playerEntity, CallbackInfo info) {
        sendPacket(playerEntity);
    }

    public void sendPacket(ServerPlayerEntity player) {
        List<EntityStrawGolem> golems = player.world.getEntitiesByClass(EntityStrawGolem.class, player.getBoundingBox().expand(30), e -> true);
        for (EntityStrawGolem golem : golems) {
            PacketHandler.INSTANCE.sendTo(new HealthPacket(golem), player);
            PacketHandler.INSTANCE.sendTo(new HoldingPacket(golem), player);
            boolean greedy = ((GoalSelectorAccessor) golem).goalSelector().getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof GolemTemptGoal);
            PacketHandler.INSTANCE.sendTo(new GreedyPacket(golem, greedy), player);
        }
    }

}
