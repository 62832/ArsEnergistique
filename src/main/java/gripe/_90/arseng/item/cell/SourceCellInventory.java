package gripe._90.arseng.item.cell;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import gripe._90.arseng.ae2.SourceKey;

public class SourceCellInventory implements StorageCell {

    private static final String AMOUNT = "amount";
    protected static final long MAX_SOURCE = 4096;

    private final ItemStack i;
    private final ISaveProvider container;

    private long sourceAmount;
    private boolean isPersisted = true;

    public SourceCellInventory(ItemStack o, ISaveProvider container) {
        this.i = o;
        this.container = container;

        this.sourceAmount = getTag().getLong(AMOUNT);
    }

    protected long getSourceAmount() {
        return this.sourceAmount;
    }

    private CompoundTag getTag() {
        return this.i.getOrCreateTag();
    }

    @Override
    public CellState getStatus() {
        if (this.sourceAmount == 0) {
            return CellState.EMPTY;
        }
        if (this.sourceAmount == MAX_SOURCE) {
            return CellState.FULL;
        }
        if (this.sourceAmount > MAX_SOURCE / 2) {
            return CellState.TYPES_FULL;
        }
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 1.0f;
    }

    protected void saveChanges() {
        this.isPersisted = false;
        if (this.container != null) {
            this.container.saveChanges();
        } else {
            // if there is no ISaveProvider, store to NBT immediately
            this.persist();
        }
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || this.sourceAmount == MAX_SOURCE) {
            return 0;
        }

        // still whatever hacky read-only thing BasicStorageCell currently does
        if (what instanceof AEItemKey itemKey) {
            var meInventory = new SourceCellInventory(itemKey.toStack(), null);
            if (!meInventory.getAvailableStacks().isEmpty()) {
                return 0;
            }
        }

        long remainingAmount = Math.max(0, MAX_SOURCE - this.sourceAmount);
        if (amount > remainingAmount) {
            amount = remainingAmount;
        }
        if (mode == Actionable.MODULATE) {
            this.sourceAmount += amount;
            saveChanges();
        }
        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var extractAmount = Math.min(Integer.MAX_VALUE, amount);
        var currentAmount = getSourceAmount();

        if (this.sourceAmount > 0 && Objects.equals(SourceKey.KEY, what)) {
            if (mode == Actionable.MODULATE) {
                this.sourceAmount = Math.max(0, this.sourceAmount - extractAmount);
                saveChanges();
            }
            return Math.min(extractAmount, currentAmount);
        }
        return 0;
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }

        if (this.sourceAmount < 0) {
            this.getTag().remove(AMOUNT);
        } else {
            this.getTag().putLong(AMOUNT, this.sourceAmount);
        }

        this.isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (this.sourceAmount > 0) {
            out.add(SourceKey.KEY, this.sourceAmount);
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return Objects.equals(what, SourceKey.KEY);
    }

    @Override
    public Component getDescription() {
        return this.i.getHoverName();
    }
}
