package gripe._90.arseng.ae2;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;

@SuppressWarnings("UnstableApiUsage")
public record MESourceStorage(BlockPos pos, BlockState state,
        GenericInternalInventory inventory) implements ISourceTile {

    @Override
    public int getTransferRate() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canAcceptSource() {
        for (var i = 0; i < inventory.size(); i++) {
            if (inventory.insert(i, SourceKey.KEY, 1, Actionable.SIMULATE) != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSource() {
        var accumulator = 0;

        for (var i = 0; i < inventory.size(); i++) {
            accumulator += inventory.extract(i, SourceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE);
        }

        return accumulator;
    }

    @Override
    public int getMaxSource() {
        var max = 0;

        for (var i = 0; i < inventory.size(); i++) {
            max += inventory.extract(i, SourceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE);
            max += inventory.insert(i, SourceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE);
        }

        return max;
    }

    @Override
    public void setMaxSource(int max) {
    }

    @Override
    public int setSource(int source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addSource(int source) {
        for (var i = 0; i < inventory.size(); i++) {
            source -= inventory.insert(i, SourceKey.KEY, source, Actionable.MODULATE);
        }
        return getSource();
    }

    @Override
    public int removeSource(int source) {
        for (var i = 0; i < inventory.size(); i++) {
            source -= inventory.extract(i, SourceKey.KEY, source, Actionable.MODULATE);
        }
        return getSource();
    }
}
