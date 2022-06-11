package com.t2pellet.strawgolem.crop;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CropRegistryImpl implements CropRegistry {

    private final Map<CropKey<?>, Object> entries = new HashMap<>();

    @Override
    public <T extends BlockState> void register(Block id, IHarvestChecker<T> harvestChecker, IHarvestLogic<T> harvestLogic, IReplantLogic<T> replantLogic) {
        StrawgolemCommon.LOG.debug("Registering crop block: {}", id);
        entries.put(new CropKey<>(id), new CropVal<>(harvestChecker, harvestLogic, replantLogic));
    }

    @Override
    public <T extends BlockEntity> void register(BlockEntityType<T> id, IHarvestChecker<T> harvestChecker, IHarvestLogic<T> harvestLogic, IReplantLogic<T> replantLogic) {
        StrawgolemCommon.LOG.debug("Registering crop block: {}", id);
        entries.put(new CropKey<>(id), new CropVal<>(harvestChecker, harvestLogic, replantLogic));
    }

    @Override
    public boolean isGrownCrop(LevelReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockEntity entity = world.getBlockEntity(pos);
        return isGrownCrop(state) || isGrownCrop(entity);
    }

    @Override
    public <T extends BlockState> boolean isGrownCrop(T state) {
        return state != null && isGrownCrop(new CropKey<>(state.getBlock()), state) && StrawgolemConfig.Harvest.isHarvestAllowed(state.getBlock());
    }

    @Override
    public <T extends BlockEntity> boolean isGrownCrop(T block) {
        return block != null && isGrownCrop(new CropKey<>(block.getType()), block) && StrawgolemConfig.Harvest.isHarvestAllowed(block.getBlockState().getBlock());
    }

    public void handleReplant(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        CropKey<Block> blockKey = new CropKey<>(state.getBlock());
        if (contains(blockKey)) {
            handleReplant(blockKey, level, pos, state);
        } else {
            BlockEntity entity = level.getBlockEntity(pos);
            CropKey<BlockEntityType> entityKey = new CropKey<>(entity.getType());
            if (contains(entityKey)) {
                handleReplant(entityKey, level, pos, entity);
            }
        }
    }

    @Override
    public List<ItemStack> handleHarvest(ServerLevel level, StrawGolem golem, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        CropKey<Block> blockKey = new CropKey<>(state.getBlock());
        if (contains(blockKey)) {
            return handleHarvest(blockKey, level, golem, pos, state);
        } else {
            BlockEntity entity = level.getBlockEntity(pos);
            CropKey<BlockEntityType> entityKey = new CropKey<>(entity.getType());
            if (contains(entityKey)) {
                return handleHarvest(entityKey, level, golem, pos, entity);
            }
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private <T> void handleReplant(CropKey<?> key, Level level, BlockPos pos, T val) {
        if (contains(key)) {
            CropVal<T> data = (CropVal<T>) entries.get(key);
            data.replantLogic.doReplant(level, pos, val);
        }
    }

    // TODO : Fix StemGrownBlock being harvestable even when manually placed
    @SuppressWarnings("unchecked")
    private <T> boolean isGrownCrop(CropKey<?> key, T val) {
        if (contains(key)) {
            CropVal<T> data = (CropVal<T>) entries.get(key);
            return data.checker.isMature(val);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> List<ItemStack> handleHarvest(CropKey<?> key, ServerLevel level, StrawGolem golem, BlockPos pos, T val) {
        if (contains(key)) {
            CropVal<T> data = (CropVal<T>) entries.get(key);
            return data.harvestLogic.doHarvest(level, golem, pos, val);
        }
        return new ArrayList<>();
    }

    private boolean contains(CropKey<?> id) {
        return id != null && id.get() != null && entries.containsKey(id);
    }

    private static class CropKey<T> {

        private final T obj;

        private CropKey(T obj) {
            this.obj = obj;
        }

        T get() {
            return obj;
        }

        @Override
        public int hashCode() {
            return get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (obj instanceof CropKey<?>) {
                CropKey<?> entry = (CropKey<?>) obj;
                return get().equals(entry.get());
            } else return get().equals(obj);
        }
    }

    private static class CropVal<T> {

        public final IHarvestChecker<T> checker;
        public final IHarvestLogic<T> harvestLogic;
        public final IReplantLogic<T> replantLogic;

        public CropVal(IHarvestChecker<T> checker, IHarvestLogic<T> harvestLogic, IReplantLogic<T> replantLogic) {
            this.checker = checker;
            this.harvestLogic = harvestLogic;
            this.replantLogic = replantLogic;
        }
    }
}
