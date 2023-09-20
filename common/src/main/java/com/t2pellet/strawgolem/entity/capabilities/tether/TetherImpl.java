package com.t2pellet.strawgolem.entity.capabilities.tether;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.tlib.common.entity.capability.AbstractCapability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;

// TODO : Consideration for when golem changes dimensions
public class TetherImpl<E extends Entity & ICapabilityHaver> extends AbstractCapability<E> implements Tether {

    private BlockPos pos;

    protected TetherImpl(E e) {
        super(e);
    }

    @Override
    public BlockPos get() {
        if (pos == null) update();
        return pos;
    }

    @Override
    public void update() {
        pos = entity.blockPosition();
    }

    @Override
    public void update(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean isTooFar() {
        return pos.distManhattan(entity.blockPosition()) > StrawgolemConfig.Behaviour.golemWanderRange.get();
    }

    @Override
    public boolean exists() {
        return pos != null;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        if (pos != null) {
            tag.put("pos", NbtUtils.writeBlockPos(pos));
        }
        return tag;
    }

    @Override
    public void readTag(Tag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        CompoundTag posTag = compoundTag.getCompound("pos");
        if (!posTag.isEmpty()) {
            pos = NbtUtils.readBlockPos(posTag);
        } else pos = null;
    }
}
