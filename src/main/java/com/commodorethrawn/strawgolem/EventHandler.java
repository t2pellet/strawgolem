package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import com.commodorethrawn.strawgolem.entity.ai.PickupGolemGoal;
import net.minecraft.block.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = Strawgolem.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class EventHandler {

	/**
	 * Handles golem building based on block placement
	 * @param event
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

	@SubscribeEvent
	public static void setPriorityChest(PlayerInteractEvent.RightClickBlock event) {
		if (!event.getWorld().isRemote
			&& event.getWorld().getTileEntity(event.getPos()) instanceof ChestTileEntity
			&& event.getPlayer().getPersistentData().contains("golemId")
			&& event.getHand() == Hand.MAIN_HAND
			&& event.getPlayer().getHeldItemMainhand().getItem() == Items.WHEAT) {
			EntityStrawGolem golem = (EntityStrawGolem) event.getWorld().getEntityByID(event.getPlayer().getPersistentData().getInt("golemId"));
			assert golem != null;
			golem.getMemory().setPriorityChest(event.getPos());
			golem.getMemory().addPosition(event.getPos());
			event.getPlayer().sendMessage(golem.getDisplayName().appendText(" will now deliver to this chest"));
		}
	}

	@SubscribeEvent
	public static void ironGolemSpawn(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof IronGolemEntity) {
			IronGolemEntity golem = (IronGolemEntity) event.getEntity();
			golem.goalSelector.addGoal(2, new PickupGolemGoal(golem, 0.8D));
		}
	}
}
