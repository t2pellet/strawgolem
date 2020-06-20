package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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
		
		if (checkStructure(worldIn, hay, pumpkin)) {
			pos = hay;
			worldIn.setBlockState(pumpkin, Blocks.AIR.getDefaultState());
			worldIn.setBlockState(hay, Blocks.AIR.getDefaultState());
			EntityStrawGolem strawGolem = new EntityStrawGolem(Registry.STRAW_GOLEM_TYPE, worldIn);
			strawGolem.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			worldIn.addEntity(strawGolem);
		}
	}

	private static boolean checkStructure(World worldIn, BlockPos hay, BlockPos pumpkin) {
		return worldIn.getBlockState(hay).getBlock() == Blocks.HAY_BLOCK
				&& worldIn.getBlockState(pumpkin).getBlock() == Blocks.CARVED_PUMPKIN;
	}

	@SubscribeEvent
	public static void onGolemHurt(LivingHurtEvent event) {
		if (!event.getEntity().getEntityWorld().isRemote
				&& event.getEntityLiving() instanceof EntityStrawGolem
				&& event.getSource() == DamageSource.SWEET_BERRY_BUSH) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onGolemHealed(PlayerInteractEvent.EntityInteract event) {
		if (!event.getWorld().isRemote && event.getTarget() instanceof EntityStrawGolem) {
			if (event.getPlayer().getHeldItemMainhand().getItem() == Items.WHEAT) {
				event.getPlayer().getHeldItemMainhand().shrink(1);
				((EntityStrawGolem) event.getTarget()).heal(1.0F);
			}
		}
	}

}
