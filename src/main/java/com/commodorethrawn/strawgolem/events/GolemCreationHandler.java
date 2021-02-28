package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.registry.CommonRegistry;
import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Objects;

public class GolemCreationHandler {

    private GolemCreationHandler() {
    }

    /**
     * Handles golem building based on block placement
     */
    public static ActionResult onGolemBuilt(PlayerEntity player, World worldIn, Hand hand, BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        Item heldItem = player.getMainHandStack().getItem();
        if (heldItem instanceof BlockItem) {
            Block heldBlock = ((BlockItem) heldItem).getBlock();
            Vector3f direction = result.getSide().getUnitVector();
            BlockPos placementPos = pos.add(direction.getX(), direction.getY(), direction.getZ());
            if (heldBlock == Blocks.CARVED_PUMPKIN) {
                BlockPos hayPos = placementPos.down();
                if (worldIn.getBlockState(hayPos).getBlock() == Blocks.HAY_BLOCK) spawnGolem(worldIn, hayPos, placementPos, result.getSide());
            } else if (heldBlock == Blocks.HAY_BLOCK) {
                BlockPos pumpkinPos = placementPos.up();
                if (worldIn.getBlockState(pumpkinPos).getBlock() == Blocks.CARVED_PUMPKIN) spawnGolem(worldIn, placementPos, pumpkinPos, result.getSide());
            }
        }
        return ActionResult.PASS;
    }

    /**
     * Handles golem building based on shearing the pumpkin
     */
    public static ActionResult onGolemBuiltAlternate(PlayerEntity player, World worldIn, Hand hand, BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        if (player.getMainHandStack().getItem() == Items.SHEARS
                && worldIn.getBlockState(pos).getBlock() == Blocks.PUMPKIN
                && worldIn.getBlockState(pos.down()).getBlock() == Blocks.HAY_BLOCK) {
            Direction facing = result.getSide();
            worldIn.setBlockState(result.getBlockPos(), Blocks.CARVED_PUMPKIN.getDefaultState().with(HorizontalFacingBlock.FACING, facing));
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
            player.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
            return spawnGolem(worldIn, pos.down(), pos, result.getSide());
        }
        return ActionResult.PASS;
    }

    /**
     * Spawns the strawgolem in the given world if theres a hay block at pos hay and pumpkin block at pos pumpkin
     *
     * @param worldIn the world
     * @param hay     position of hay
     * @param pumpkin position of pumpkin
     */
    private static ActionResult spawnGolem(World worldIn, BlockPos hay, BlockPos pumpkin, Direction facing) {
        if (!worldIn.isClient) {
            ServerWorld world = (ServerWorld) worldIn;
            world.setBlockState(pumpkin, Blocks.AIR.getDefaultState());
            world.setBlockState(hay, Blocks.AIR.getDefaultState());
            EntityStrawGolem strawGolem = CommonRegistry.strawGolemEntityType().create(world);
            strawGolem.refreshPositionAndAngles(hay, facing.getHorizontal(), 0.0F);
            world.spawnEntity(strawGolem);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
