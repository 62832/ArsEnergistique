package gripe._90.arseng.me.cell;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

public class SourceCellInventory implements StorageCell {
    private static final String AMOUNT = "amount";

    private final ISourceCellItem cellType;
    private final ItemStack i;
    private final ISaveProvider container;

    private long sourceAmount;
    private boolean isPersisted = true;

    public SourceCellInventory(ISourceCellItem cellType, ItemStack o, ISaveProvider container) {
        this.cellType = cellType;
        this.i = o;
        this.container = container;

        this.sourceAmount = getTag().getLong(AMOUNT);
    }

    public long getTotalBytes() {
        return cellType.getTotalBytes();
    }

    public long getUsedBytes() {
        var amountPerByte = SourceKeyType.TYPE.getAmountPerByte();
        return (sourceAmount + amountPerByte - 1) / amountPerByte;
    }

    public long getMaxSource() {
        return cellType.getTotalBytes() * SourceKeyType.TYPE.getAmountPerByte();
    }

    private CompoundTag getTag() {
        return i.getOrCreateTag();
    }

    @Override
    public CellState getStatus() {
        if (sourceAmount == 0) {
            return CellState.EMPTY;
        }

        if (sourceAmount == getMaxSource()) {
            return CellState.FULL;
        }

        if (sourceAmount > getMaxSource() / 2) {
            return CellState.TYPES_FULL;
        }

        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return cellType.getIdleDrain();
    }

    protected void saveChanges() {
        isPersisted = false;

        if (container != null) {
            container.saveChanges();
        } else {
            // if there is no ISaveProvider, store to NBT immediately
            persist();
        }
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !(what instanceof SourceKey) || sourceAmount == getMaxSource()) {
            return 0;
        }

        long remainingAmount = Math.max(0, getMaxSource() - sourceAmount);

        if (amount > remainingAmount) {
            amount = remainingAmount;
        }

        if (mode == Actionable.MODULATE) {
            sourceAmount += amount;
            saveChanges();
        }

        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var extractAmount = Math.min(Integer.MAX_VALUE, amount);
        var currentAmount = sourceAmount;

        if (sourceAmount > 0 && Objects.equals(SourceKey.KEY, what)) {
            if (mode == Actionable.MODULATE) {
                sourceAmount = Math.max(0, sourceAmount - extractAmount);
                saveChanges();
            }

            return Math.min(extractAmount, currentAmount);
        }

        return 0;
    }

    @Override
    public void persist() {
        if (isPersisted) {
            return;
        }

        if (sourceAmount < 0) {
            getTag().remove(AMOUNT);
        } else {
            getTag().putLong(AMOUNT, sourceAmount);
        }

        isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (sourceAmount > 0) {
            out.add(SourceKey.KEY, sourceAmount);
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return what instanceof SourceKey;
    }

    @Override
    public Component getDescription() {
        return i.getHoverName();
    }
}
