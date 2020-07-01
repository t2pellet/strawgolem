package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class GolemEventHandler {
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
}
