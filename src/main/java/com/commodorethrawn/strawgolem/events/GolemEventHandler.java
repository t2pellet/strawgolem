package com.commodorethrawn.strawgolem.events;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import com.commodorethrawn.strawgolem.network.MessageLifespan;
import com.commodorethrawn.strawgolem.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = Strawgolem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemEventHandler {

    private static final String GOLEM = "golemId";

    private GolemEventHandler() {
    }

    /**
     * Cancels damage from sweet berry bushes
     *
     * @param event the attacked event
     */
    @SubscribeEvent
    public static void onGolemHurt(LivingAttackEvent event) {
        if (!event.getEntity().getEntityWorld().isRemote
                && event.getEntityLiving() instanceof EntityStrawGolem
                && event.getSource() == DamageSource.SWEET_BERRY_BUSH) {
            event.setCanceled(true);
        }
    }

    /**
     * Sends the player update packets to properly display all the nearby strawgolems
     */
    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof PlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            List<EntityStrawGolem> golems = player.world.getEntitiesWithinAABB(EntityStrawGolem.class, player.getBoundingBox().grow(25));
            for (EntityStrawGolem golem : golems) {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageLifespan(golem));
            }
        }
    }

    /**
     * Sets the chest that golem will always prioritize going to deliver
     *
     * @param event the right click block event
     */
    @SubscribeEvent
    public static void setPriorityChest(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
            PlayerEntity player = event.getPlayer();
            if (tileEntity != null
                    && event.getHand() == Hand.MAIN_HAND
                    && player.getHeldItemMainhand().getItem() == Items.WHEAT
                    && tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent()
                    && player.getPersistentData().contains(GOLEM)) {
                EntityStrawGolem golem = (EntityStrawGolem) event.getWorld().getEntityByID(player.getPersistentData().getInt(GOLEM));
                if (golem != null) {
                    golem.getMemory().setPriorityChest(event.getPos());
                    golem.getMemory().addPosition(event.getWorld(), event.getPos());
                    StringTextComponent message = new StringTextComponent(golem.getDisplayName().getString() + " will now deliver to this chest");
                    event.getPlayer().sendMessage(message, Util.field_240973_b_);
                    player.getPersistentData().remove(GOLEM);
                }
            }
        }
    }
}
