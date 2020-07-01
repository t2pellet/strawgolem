package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Registry;
import com.commodorethrawn.strawgolem.Strawgolem;
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
public class IronGolemHandler {

	@SubscribeEvent
	public static void ironGolemSpawn(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof IronGolemEntity) {
			IronGolemEntity golem = (IronGolemEntity) event.getEntity();
			golem.goalSelector.addGoal(2, new PickupGolemGoal(golem, 0.8D));
		}
	}
}
