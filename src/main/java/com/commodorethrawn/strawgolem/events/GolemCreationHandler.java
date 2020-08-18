package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Registry;
import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemCreationHandler {
    /**
     * Handles golem building based on block placement
     *
     * @param event the placement event
     */
    @SubscribeEvent
    public static void onGolemBuilt(BlockEvent.EntityPlaceEvent event) {
        World worldIn = (World) event.getWorld();
        BlockPos pos = event.getPos();
        Block block = event.getState().getBlock();

        BlockPos pumpkin;
        BlockPos hay;

        if (block == Blocks.CARVED_PUMPKIN) {
            pumpkin = pos;
            hay = pos.down();
        } else if (block == Blocks.HAY_BLOCK) {
            pumpkin = pos.up();
            hay = pos;
        } else return;
        spawnGolem(worldIn, hay, pumpkin);
    }

    /**
     * Handles golem building based on shearing the pumpkin
     *
     * @param event the right click block event
     */
    @SubscribeEvent
    public static void onGolemBuiltAlternate(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer().getHeldItemMainhand().getItem() == Items.SHEARS
                && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.PUMPKIN) {
            Direction facing = event.getPlayer().getHorizontalFacing().getOpposite();
            event.getWorld().setBlockState(event.getPos(), Blocks.CARVED_PUMPKIN.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, facing));
            event.setCanceled(true);
            event.getWorld().playSound(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
            event.getPlayer().getHeldItemMainhand().damageItem(1, event.getPlayer(), p -> p.sendBreakAnimation(Hand.MAIN_HAND));
            spawnGolem(event.getWorld(), event.getPos().down(), event.getPos());
        }
    }

    /**
     * Spawns the strawgolem in the given world if theres a hay block at pos hay and pumpkin block at pos pumpkin
     *
     * @param worldIn the world
     * @param hay     position of hay
     * @param pumpkin position of pumpkin
     */
    private static void spawnGolem(World worldIn, BlockPos hay, BlockPos pumpkin) {
        if (worldIn.getBlockState(hay).getBlock() == Blocks.HAY_BLOCK
                && worldIn.getBlockState(pumpkin).getBlock() == Blocks.CARVED_PUMPKIN) {
            worldIn.setBlockState(pumpkin, Blocks.AIR.getDefaultState());
            worldIn.setBlockState(hay, Blocks.AIR.getDefaultState());
            EntityStrawGolem strawGolem = new EntityStrawGolem(Registry.STRAW_GOLEM_TYPE, worldIn);
            strawGolem.setPosition(hay.getX() + 0.5D, hay.getY(), hay.getZ() + 0.5D);
            worldIn.addEntity(strawGolem);
        }
    }
}
