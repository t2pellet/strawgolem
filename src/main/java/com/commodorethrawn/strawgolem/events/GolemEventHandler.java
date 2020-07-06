package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.strawgolem.EntityStrawGolem;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
