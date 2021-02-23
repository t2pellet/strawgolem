package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.config.ConfigHelper;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GolemChestHandler {

    private static final Map<UUID, Integer> playerToGolemMap = new HashMap<>();

    private GolemChestHandler() {
    }

    public static void addMapping(UUID player, Integer golem) {
        playerToGolemMap.put(player, golem);
    }

    /**
     * Sets the chest that golem will always prioritize going to deliver
     */
    public static ActionResult setPriorityChest(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(blockHitResult.getBlockPos());
            if (hand == Hand.MAIN_HAND
                    && blockEntity instanceof Inventory
                    && playerEntity.isSneaking()
                    && playerEntity.getMainHandStack().isEmpty()
                    && playerToGolemMap.containsKey(playerEntity.getUuid())) {
                EntityStrawGolem golem = (EntityStrawGolem) world.getEntityById(playerToGolemMap.get(playerEntity.getUuid()));
                if (golem != null) {
                    golem.getMemory().setPriorityChest(blockHitResult.getBlockPos());
                    golem.getMemory().addPosition(world, blockHitResult.getBlockPos());
                    Text text = new TranslatableText("strawgolem.deliver", golem.getDisplayName().getString());
                    playerEntity.sendMessage(text, true);
                    playerToGolemMap.remove(playerEntity.getUuid());
                    // Tether to the priority chest
                    if (ConfigHelper.isTetherEnabled()) {
                        BlockPos anchorPos = blockHitResult.getBlockPos();
                        Strawgolem.logger.debug(golem.getEntityId() + " setting new anchor " + anchorPos);
                        golem.getTether().set(golem.world, anchorPos);
                    }
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }
}
