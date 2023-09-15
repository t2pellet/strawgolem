package com.t2pellet.strawgolem.entity.capabilities.harvester;

import com.t2pellet.strawgolem.util.crop.SeedUtil;
import com.t2pellet.strawgolem.world.WorldCrops;
import com.t2pellet.tlib.Services;
import com.t2pellet.tlib.common.entity.capability.AbstractCapability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;

class HarvesterImpl<E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Harvester {

    private BlockPos harvestPos = null;

    protected HarvesterImpl(E e) {
        super(e);
    }

    @Override
    public void harvest(BlockPos pos) {
        if (pos != harvestPos) {
            harvestPos = pos;
            synchronize();
            Services.SIDE.scheduleServer(24, this::completeHarvest);
        }
    }

    @Override
    public boolean isHarvesting() {
        return harvestPos != null;
    }

    private void completeHarvest() {
        if (!e.level.isClientSide) {
            BlockState state = e.level.getBlockState(harvestPos);
            BlockState defaultState = state.getBlock().defaultBlockState();
            e.setItemSlot(EquipmentSlot.MAINHAND, pickupLoot(state));
            harvestBlock(harvestPos, defaultState);
            harvestPos = null;
            WorldCrops.of((ServerLevel) e.level).remove(harvestPos);
            synchronize();
        } else harvestPos = null;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        if (harvestPos != null) {
            tag.put("pos", NbtUtils.writeBlockPos(harvestPos));
        }
        return tag;
    }

    @Override
    public void readTag(Tag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        CompoundTag posTag = compoundTag.getCompound("pos");
        if (!posTag.isEmpty()) {
            harvestPos = NbtUtils.readBlockPos(posTag);
        } else harvestPos = null;
    }

    private void harvestBlock(BlockPos blockPos, BlockState defaultState) {
        e.level.destroyBlock(blockPos, false, e);
        e.level.setBlockAndUpdate(blockPos, defaultState);
        e.level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(e, defaultState));
    }

    private ItemStack pickupLoot(BlockState state) {
        LootContext.Builder builder = new LootContext.Builder((ServerLevel) e.level).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withParameter(LootContextParams.ORIGIN, e.position());
        List<ItemStack> drops = state.getDrops(builder);
        Optional<ItemStack> pickupStack = drops.stream().filter((d) -> !SeedUtil.isSeed(d.getItem()) || d.getItem().isEdible()).findFirst();
        return pickupStack.orElseGet(() -> ItemStack.EMPTY);
    }
}
