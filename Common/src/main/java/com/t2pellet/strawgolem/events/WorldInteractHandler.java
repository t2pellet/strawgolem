package com.t2pellet.strawgolem.events;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.registry.CommonRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
     * Handles golem building based on block placement
     */
    public static InteractionResult onGolemBuilt(Player player, Level worldIn, InteractionHand hand, BlockHitResult result) {
        if (!worldIn.isClientSide && StrawgolemConfig.Creation.isGolemConstructionEnabled()) {
            BlockPos pos = result.getBlockPos();
            Item heldItem = player.getMainHandItem().getItem();
            if (heldItem instanceof BlockItem) {
                Block heldBlock = ((BlockItem) heldItem).getBlock();
                Direction direction = result.getDirection();
                BlockPos placementPos = pos.offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
                if (StrawgolemConfig.Creation.isHeadBlock(heldBlock)) {
                    BlockPos bodyPos = placementPos.below();
                    if (StrawgolemConfig.Creation.isBodyBlock(worldIn.getBlockState(bodyPos).getBlock())) {
                        spawnStrawGolem(worldIn, bodyPos, placementPos, result.getDirection());
                    }
                } else if (StrawgolemConfig.Creation.isBodyBlock(heldBlock)) {
                    BlockPos headPos = placementPos.above();
                    if (StrawgolemConfig.Creation.isHeadBlock(worldIn.getBlockState(headPos).getBlock())) {
                        spawnStrawGolem(worldIn, placementPos, headPos, result.getDirection());
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Handles golem building based on shearing the pumpkin
     */
    public static InteractionResult onGolemBuiltAlternate(Player player, Level worldIn, InteractionHand hand, BlockHitResult result) {
        if (!worldIn.isClientSide && StrawgolemConfig.Creation.isGolemConstructionEnabled() && StrawgolemConfig.Creation.isShearConstructionEnabled()) {
            BlockPos pos = result.getBlockPos();
            if (player.getMainHandItem().getItem() == Items.SHEARS
                    && worldIn.getBlockState(pos).getBlock() == Blocks.PUMPKIN
                    && StrawgolemConfig.Creation.isBodyBlock(worldIn.getBlockState(pos.below()).getBlock())) {
                worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F, true);
                player.getMainHandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                return spawnStrawGolem(worldIn, pos.below(), pos, result.getDirection());
            }
        }
        return InteractionResult.PASS;
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
                EntityStrawGolem golem = (EntityStrawGolem) world.getEntity(playerToGolemMap.get(Player.getUUID()));
                if (golem != null) {
                    golem.getMemory().setPriorityChest(blockHitResult.getBlockPos());
                    golem.getMemory().addPosition(world, blockHitResult.getBlockPos());
                    Component text = Component.translatable("strawgolem.deliver", golem.getDisplayName().getString());
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

    /**
     * Spawns the strawgolem in the given world
     *
     * @param worldIn the world
     * @param hay     position of hay
     * @param pumpkin position of pumpkin
     * @param facing  the facing direction for the golem
     * @return the ActionResult
     */
    private static InteractionResult spawnStrawGolem(Level worldIn, BlockPos hay, BlockPos pumpkin, Direction facing) {
        if (!worldIn.isClientSide()) {
            ServerLevel world = (ServerLevel) worldIn;
            world.setBlockAndUpdate(pumpkin, Blocks.AIR.defaultBlockState());
            world.setBlockAndUpdate(hay, Blocks.AIR.defaultBlockState());
            EntityStrawGolem strawGolem = CommonRegistry.Entities.getStrawGolemType().create(world);
            strawGolem.setXRot(facing.get2DDataValue());
            strawGolem.setYHeadRot(0.0F);
            strawGolem.setPos(hay.getX() + 0.5, hay.getY() + 0.5, hay.getZ() + 0.5);
            worldIn.addFreshEntity(strawGolem);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
