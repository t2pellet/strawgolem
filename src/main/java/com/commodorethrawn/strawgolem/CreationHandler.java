package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.entity.EntityRegistry;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Strawgolem.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CreationHandler {
	
	@SubscribeEvent
	public static void onBlockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
		World worldIn = (World)event.getWorld();
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
		
		if (checkStructure(worldIn, hay, pumpkin)) {
			pos = hay;
			worldIn.setBlockState(pumpkin, Blocks.AIR.getDefaultState());
			worldIn.setBlockState(hay, Blocks.AIR.getDefaultState());
			EntityStrawGolem strawGolem = new EntityStrawGolem(EntityRegistry.STRAW_GOLEM_TYPE, worldIn);
            strawGolem.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			worldIn.addEntity(strawGolem);
		}
	}

	private static boolean checkStructure(World worldIn, BlockPos hay, BlockPos pumpkin) {
		return worldIn.getBlockState(hay).getBlock() == Blocks.HAY_BLOCK
                && worldIn.getBlockState(pumpkin).getBlock() == Blocks.CARVED_PUMPKIN;
	}
	
}
