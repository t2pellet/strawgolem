package nivoridocs.strawgolem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nivoridocs.strawgolem.entity.EntityStrawGolem;

@EventBusSubscriber
public class StrawGolemCreationEventHandler {
	
	@SubscribeEvent
	public static void onBlockPlaceEvent(BlockEvent.PlaceEvent event) {
		World worldIn = event.getWorld();
		BlockPos pos = event.getPos();
		Block block = event.getState().getBlock();
		
		BlockPos pumpkin;
		BlockPos hay;
		
		if (block == Blocks.PUMPKIN) {
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
			EntityStrawGolem strawGolem = new EntityStrawGolem(worldIn);
			strawGolem.setPosition(getCoord(pos.getX()), getCoord(pos.getY()), pos.getZ());
			worldIn.spawnEntity(strawGolem);
		}
	}
	
	private static double getCoord(int c) {
		return c + Math.signum(c)*0.5D;
	}
	
	private static boolean checkStructure(World worldIn, BlockPos hay, BlockPos pumpkin) {
		return worldIn.getBlockState(hay).getBlock() == Blocks.HAY_BLOCK
				&& worldIn.getBlockState(pumpkin).getBlock() == Blocks.PUMPKIN;
	}
	
}
