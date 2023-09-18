package com.t2pellet.strawgolem.entity.capabilities.harvester;

import com.t2pellet.strawgolem.util.crop.CropUtil;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;

class HarvesterImpl<E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Harvester {


    private boolean harvestingBlock = false;
    private BlockPos harvestPos = null;

    protected HarvesterImpl(E e) {
        super(e);
    }

    @Override
    public void harvest(BlockPos pos) {
        if (pos != harvestPos) {
            harvestPos = pos;
            harvestingBlock = e.level.getBlockState(pos).getBlock() instanceof StemGrownBlock;
            Services.SIDE.scheduleServer(24, this::completeHarvest);
            synchronize();
        }
    }

    @Override
    public boolean isHarvesting() {
        return harvestPos != null;
    }

    @Override
    public boolean isHarvestingBlock() {
        return isHarvesting() && harvestingBlock;
    }

    @Override
    public void completeHarvest() {
        if (!e.level.isClientSide && CropUtil.isGrownCrop(e.level, harvestPos) && isHarvesting()) {
            BlockState state = e.level.getBlockState(harvestPos);
            BlockState defaultState = state.getBlock() instanceof StemGrownBlock ? Blocks.AIR.defaultBlockState() : state.getBlock().defaultBlockState();
            e.setItemSlot(EquipmentSlot.MAINHAND, pickupLoot(state));
            harvestCrop(harvestPos, defaultState);
            WorldCrops.of((ServerLevel) e.level).remove(harvestPos);
        } else {
            harvestPos = null;
            harvestingBlock = false;
        }
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        if (harvestPos != null) {
            tag.put("pos", NbtUtils.writeBlockPos(harvestPos));
        }
        tag.putBoolean("isBlock", harvestingBlock);
        return tag;
    }

    @Override
    public void readTag(Tag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        CompoundTag posTag = compoundTag.getCompound("pos");
        if (!posTag.isEmpty()) {
            harvestPos = NbtUtils.readBlockPos(posTag);
        } else harvestPos = null;
        harvestingBlock = compoundTag.getBoolean("isBlock");
    }

    private void harvestCrop(BlockPos blockPos, BlockState defaultState) {
        e.level.destroyBlock(blockPos, false, e);
        e.level.setBlockAndUpdate(blockPos, defaultState);
        e.level.gameEvent(defaultState.isAir() ? GameEvent.BLOCK_DESTROY : GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(e, defaultState));
        harvestPos = null;
        harvestingBlock = false;
    }

    private ItemStack pickupLoot(BlockState state) {
        if (state.getBlock() instanceof StemGrownBlock) return new ItemStack(state.getBlock().asItem(), 1);
        LootContext.Builder builder = new LootContext.Builder((ServerLevel) e.level).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withParameter(LootContextParams.ORIGIN, e.position());
        List<ItemStack> drops = state.getDrops(builder);
        Optional<ItemStack> pickupStack = drops.stream().filter((d) -> !SeedUtil.isSeed(d) || d.getItem().isEdible()).findFirst();
        return pickupStack.orElse(ItemStack.EMPTY);
    }
}
