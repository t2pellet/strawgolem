package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = Strawgolem.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class EventHandler {

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

	@SubscribeEvent
	public static void onGolemBuiltAlternate(PlayerInteractEvent.RightClickBlock event) {
		if (event.getPlayer().getHeldItemOffhand().getItem() == Items.SHEARS
			&& event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.PUMPKIN) {
			spawnGolem(event.getWorld(), event.getPos(), event.getPos().down());
		}
	}


	private static void spawnGolem(World worldIn, BlockPos hay, BlockPos pumpkin) {
		if (worldIn.getBlockState(hay).getBlock() == Blocks.HAY_BLOCK
				&& worldIn.getBlockState(pumpkin).getBlock() == Blocks.CARVED_PUMPKIN) {
			BlockPos pos = hay;
			worldIn.setBlockState(pumpkin, Blocks.AIR.getDefaultState());
			worldIn.setBlockState(hay, Blocks.AIR.getDefaultState());
			EntityStrawGolem strawGolem = new EntityStrawGolem(Registry.STRAW_GOLEM_TYPE, worldIn);
			strawGolem.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			worldIn.addEntity(strawGolem);
		}
	}

	@SubscribeEvent
	public static void onGolemHurt(LivingAttackEvent event) {
		if (!event.getEntity().getEntityWorld().isRemote
				&& event.getEntityLiving() instanceof EntityStrawGolem
				&& event.getSource() == DamageSource.SWEET_BERRY_BUSH) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void cropGrown(BlockEvent.CropGrowEvent.Post event) {
		if (!event.getWorld().isRemote()) {
			List<EntityStrawGolem> golemList = event.getWorld().getEntitiesWithinAABB(EntityStrawGolem.class, new AxisAlignedBB(event.getPos()).grow(StrawgolemConfig.getSearchRangeHorizontal(), StrawgolemConfig.getSearchRangeVertical(), StrawgolemConfig.getSearchRangeHorizontal()));
			for (EntityStrawGolem golem : golemList) {
				RayTraceContext ctx = new RayTraceContext(golem.getPositionVec().add(0, 1, 0), new Vec3d(event.getPos()), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, golem);
				if (event.getWorld().rayTraceBlocks(ctx).getPos().equals(event.getPos())) {
					golem.setHarvesting(event.getPos());
					break;
				}
			}
		}
	}

}
