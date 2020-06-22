package com.commodorethrawn.strawgolem;

import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Random;

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
				EntityStrawGolem target = (EntityStrawGolem) event.getTarget();
				if (target.getHealth() != target.getMaxHealth()) {
					event.getPlayer().getHeldItemMainhand().shrink(1);
					target.heal(1.0F);
					spawnHealParticles(event.getTarget().prevPosX, event.getTarget().prevPosY, event.getTarget().prevPosZ);

				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static void spawnHealParticles(double x, double y, double z) {
		ClientWorld world = Minecraft.getInstance().world;
		Random rand = new Random();
		world.addParticle(ParticleTypes.HEART, x + rand.nextDouble() - 0.5, y + 0.4D, z + rand.nextDouble() - 0.5, 0, 0, 0);
	}

}
