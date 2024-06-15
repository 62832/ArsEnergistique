package gripe._90.arseng.block.entity;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.storage.StorageHelper;
import appeng.api.util.AECableType;
import appeng.hooks.ticking.TickHandler;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.me.key.SourceKey;

public class MESourceJarBlockEntity extends SourceJarTile implements IGridConnectedBlockEntity {
    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, BlockEntityNodeListener.INSTANCE)
            .setFlags(GridFlags.REQUIRE_CHANNEL)
            .setVisualRepresentation(ArsEngBlocks.ME_SOURCE_JAR)
            .setInWorldNode(true)
            .setTagName("proxy");
    private final IActionSource actionSource = IActionSource.ofMachine(getMainNode()::getNode);

    private boolean setChangedQueued;
    private boolean saving;

    public MESourceJarBlockEntity(BlockPos pos, BlockState state) {
        super(ArsEngBlocks.ME_SOURCE_JAR_ENTITY, pos, state);
    }

    @Override
    public int addSource(int source) {
        var grid = getMainNode().getGrid();
        return grid != null && getMainNode().isActive()
                ? (int) StorageHelper.poweredInsert(
                        grid.getEnergyService(),
                        grid.getStorageService().getInventory(),
                        SourceKey.KEY,
                        source,
                        actionSource)
                : 0;
    }

    public int removeSource(int source) {
        var grid = getMainNode().getGrid();
        return grid != null && getMainNode().isActive()
                ? (int) StorageHelper.poweredExtraction(
                        grid.getEnergyService(),
                        grid.getStorageService().getInventory(),
                        SourceKey.KEY,
                        source,
                        actionSource)
                : 0;
    }

    @Override
    public int getSource() {
        var grid = getMainNode().getGrid();

        if (grid == null || saving) {
            return super.getSource();
        }

        return getMainNode().isActive()
                ? (int) grid.getStorageService()
                        .getInventory()
                        .extract(SourceKey.KEY, Integer.MAX_VALUE, Actionable.SIMULATE, actionSource)
                : 0;
    }

    @Override
    public int getMaxSource() {
        var grid = getMainNode().getGrid();

        if (grid == null || saving) {
            return super.getMaxSource();
        }

        var oldSource = super.getSource();
        var oldSourceCap = super.getMaxSource();
        var sourceCap = 0L;

        if (getMainNode().isActive()) {
            var storage = grid.getStorageService().getInventory();
            super.setSource((int) storage.extract(SourceKey.KEY, Integer.MAX_VALUE, Actionable.SIMULATE, actionSource));
            sourceCap = storage.extract(SourceKey.KEY, Integer.MAX_VALUE, Actionable.SIMULATE, actionSource)
                    + storage.insert(SourceKey.KEY, Integer.MAX_VALUE, Actionable.SIMULATE, actionSource);
        } else {
            super.setSource(0);
        }

        if (oldSource != super.getSource() || oldSourceCap != sourceCap) {
            setChanged();
        }

        return sourceCap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sourceCap;
    }

    @Override
    public int getTransferRate() {
        return getMainNode().isActive() ? super.getTransferRate() : 0;
    }

    @Override
    public boolean canAcceptSource() {
        return super.canAcceptSource() && getMainNode().isActive();
    }

    @Override
    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    @Override
    public void saveChanges() {
        if (level == null) {
            return;
        }

        if (level.isClientSide()) {
            setChanged();
        } else {
            level.blockEntityChanged(worldPosition);

            if (!setChangedQueued) {
                TickHandler.instance().addCallable(null, this::setChangedAtEndOfTick);
                setChangedQueued = true;
            }
        }
    }

    private void setChangedAtEndOfTick() {
        setChanged();
        setChangedQueued = false;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        try {
            saving = true;
            super.saveAdditional(tag);
        } finally {
            saving = false;
        }

        getMainNode().saveToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        getMainNode().loadFromNBT(tag);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        getMainNode().destroy();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        GridHelper.onFirstTick(this, be -> be.getMainNode().create(be.getLevel(), be.getBlockPos()));
    }

    public int calculateComparatorLevel() {
        var grid = getMainNode().getGrid();
        var currentSource = 0L;
        var freeSource = 0L;

        if (grid == null) {
            currentSource = super.getSource();
            freeSource = super.getMaxSource() - currentSource;
        } else if (getMainNode().isActive()) {
            var storage = grid.getStorageService().getInventory();
            currentSource = storage.extract(SourceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, actionSource);
            freeSource = storage.insert(SourceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, actionSource);
        }

        return currentSource != 0 ? (int) Math.ceil(1 / (1 + (double) freeSource / currentSource) * 15) : 0;
    }

    @Override
    public boolean updateBlock() {
        if (level != null) {
            var state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
            return true;
        }

        return false;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (getMainNode().hasGridBooted()) {
            getMaxSource();
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable(
                "ars_nouveau.source_jar.fullness", Math.min((getSource() * 100) / getMaxSource(), 100)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBlockState(@NotNull BlockState state) {
        var previousOrientation = BlockOrientation.get(getBlockState());
        super.setBlockState(state);
        var newOrientation = BlockOrientation.get(getBlockState());

        if (previousOrientation != newOrientation) {
            getMainNode().setExposedOnSides(getGridConnectableSides(newOrientation));
        }
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return Set.of(Direction.UP, Direction.DOWN, orientation.getSide(RelativeSide.BACK));
    }
}
