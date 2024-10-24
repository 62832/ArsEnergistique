package gripe._90.arseng.me.cell;

import java.util.Objects;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;

import gripe._90.arseng.definition.ArsEngComponents;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

public class SourceCellInventory implements StorageCell {
    private final ISourceCellItem cell;
    private final ItemStack stack;
    private final ISaveProvider container;

    private long sourceAmount;
    private boolean isPersisted = true;

    public SourceCellInventory(ISourceCellItem cell, ItemStack stack, ISaveProvider container) {
        this.cell = cell;
        this.stack = stack;
        this.container = container;

        sourceAmount = stack.getOrDefault(ArsEngComponents.SOURCE_CELL_AMOUNT, 0L);
    }

    public long getTotalBytes() {
        return cell.getTotalBytes();
    }

    public long getUsedBytes() {
        var amountPerByte = SourceKeyType.TYPE.getAmountPerByte();
        return (sourceAmount + amountPerByte - 1) / amountPerByte;
    }

    public long getMaxSource() {
        return cell.getTotalBytes() * SourceKeyType.TYPE.getAmountPerByte();
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
        return cell.getIdleDrain();
    }

    public IUpgradeInventory getUpgrades() {
        return cell.getUpgrades(stack);
    }

    private void saveChanges() {
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
        if (amount == 0 || !(what instanceof SourceKey)) {
            return 0;
        }

        var inserted = Math.min(amount, Math.max(0, getMaxSource() - sourceAmount));

        if (mode == Actionable.MODULATE) {
            sourceAmount += inserted;
            saveChanges();
        }

        return getUpgrades().isInstalled(AEItems.VOID_CARD) ? amount : inserted;
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
            stack.remove(ArsEngComponents.SOURCE_CELL_AMOUNT);
        } else {
            stack.set(ArsEngComponents.SOURCE_CELL_AMOUNT, sourceAmount);
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
    public Component getDescription() {
        return stack.getHoverName();
    }
}
