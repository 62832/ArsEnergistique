package gripe._90.arseng.me.storage;

import com.google.common.primitives.Ints;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.me.key.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class GenericStackSourceStorage implements IAdvancedSourceTile {
    private final GenericInternalInventory inv;

    public GenericStackSourceStorage(GenericInternalInventory inv) {
        this.inv = inv;
    }

    @Override
    public int getTransferRate() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canAcceptSource() {
        return insert(1, Actionable.SIMULATE) != 0;
    }

    @Override
    public int getSource() {
        return extract(Integer.MAX_VALUE, Actionable.SIMULATE);
    }

    @Override
    public int getMaxSource() {
        var slots = 0;

        for (var i = 0; i < inv.size(); i++) {
            var key = inv.getKey(i);

            if (key == null || key == SourceKey.KEY) {
                slots += 1;
            }
        }

        return Ints.saturatedCast(slots * inv.getMaxAmount(SourceKey.KEY));
    }

    @Override
    public void setMaxSource(int max) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setSource(int source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addSource(int source) {
        return insert(source, Actionable.MODULATE);
    }

    @Override
    public int removeSource(int source) {
        return extract(source, Actionable.MODULATE);
    }

    private int insert(int amount, Actionable mode) {
        var inserted = 0;

        for (var i = 0; i < inv.size() && inserted < amount; ++i) {
            inserted += (int) inv.insert(i, SourceKey.KEY, amount - inserted, mode);
        }

        return inserted;
    }

    private int extract(int amount, Actionable mode) {
        var extracted = 0;

        for (var i = 0; i < inv.size() && extracted < amount; ++i) {
            extracted += (int) inv.extract(i, SourceKey.KEY, amount - extracted, mode);
        }

        return extracted;
    }

    @Override
    public boolean relayCanTakePower() {
        return true;
    }

    @Override
    public boolean sourcelinksCanProvidePower() {
        return true;
    }
}
