package com.t2pellet.strawgolem.events;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldInteractHandler {

    private static final Map<UUID, Integer> playerToGolemMap = new HashMap<>();

    private WorldInteractHandler() {
    }

    public static void addMapping(UUID player, Integer golem) {
        playerToGolemMap.put(player, golem);
    }

    /**
     * Sets the chest that golem will always prioritize going to deliver
     */
    public static InteractionResult setPriorityChest(Player Player, Level world, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(blockHitResult.getBlockPos());
            Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            if (hand == InteractionHand.MAIN_HAND
                    && blockEntity instanceof Container
                    && StrawgolemConfig.Delivery.isDeliveryAllowed(block)
                    && Player.isShiftKeyDown()
                    && Player.getMainHandItem().isEmpty()
                    && playerToGolemMap.containsKey(Player.getUUID())) {
                StrawGolem golem = (StrawGolem) world.getEntity(playerToGolemMap.get(Player.getUUID()));
                if (golem != null) {
                    golem.getMemory().setPriorityChest(blockHitResult.getBlockPos());
                    golem.getMemory().addPosition(world, blockHitResult.getBlockPos());
                    Component text = new TranslatableComponent("strawgolem.deliver", golem.getDisplayName().getString());
                    Player.displayClientMessage(text, true);
                    playerToGolemMap.remove(Player.getUUID());
                    // Tether to the priority chest
                    if (StrawgolemConfig.Tether.isTetherEnabled()) {
                        BlockPos anchorPos = blockHitResult.getBlockPos();
                        StrawgolemCommon.LOG.debug(golem.getId() + " setting new anchor " + anchorPos);
                        golem.getTether().set(golem.level, anchorPos);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
