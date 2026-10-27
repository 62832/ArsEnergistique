package gripe._90.arseng.me.misc;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageHelper;
import appeng.helpers.InterfaceLogic;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record DirectInterfaceMapInventory(InterfaceLogic logic, IActionSource source) implements IItemHandler, IMapInventory {
    @Override
    public ItemStack insertStack(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }

        var key = AEItemKey.of(stack);
        // `stack` was already checked to be non-empty
        assert key != null;

        if (logic.getConfig().isEmpty()) {
            var node = logic.getActionableNode();
            if (node != null && node.isPowered()) {
                var inserted = StorageHelper.poweredInsert(node.getGrid().getEnergyService(), logic.getInventory(), key, stack.getCount(), source, Actionable.ofSimulate(simulate));
                stack.shrink((int) inserted);
            }
        }

        if (!stack.isEmpty()) {
            var inserted = logic.getStorage().insert(key, stack.getCount(), Actionable.ofSimulate(simulate), source);
            stack.shrink((int) inserted);
        }

        return stack;
    }

    @Override
    public boolean hasExistingSlotsForInsertion(ItemStack stack) {
        return logic.getInventory().isPreferredStorageFor(AEItemKey.of(stack), source);
    }

    @Override
    public ItemStack extractByItem(Item item, int count, boolean simulate, Predicate<ItemStack> filter) {
         for (var stack : logic.getInventory().getAvailableStacks()) {
             if (stack.getKey() instanceof AEItemKey itemKey && itemKey.is(item) && filter.test(itemKey.getReadOnlyStack())) {
                 var toExtract = Math.min(count, (int) stack.getLongValue());
                 if (!simulate) {
                     logic.getInventory().extract(itemKey, toExtract, Actionable.MODULATE, source);
                 }
                 return itemKey.toStack(toExtract);
             }
         }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemScroll.SortPref getInsertionPreference(ItemStack stack) {
        return logic.getInventory().isPreferredStorageFor(AEItemKey.of(stack), source) ? ItemScroll.SortPref.HIGHEST : ItemScroll.SortPref.LOW;
    }

    @Override
    public int getSlots() {
        return logic.getStorage().size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot >= this.getSlots()) {
            return ItemStack.EMPTY;
        }

        var stack = logic.getStorage().getStack(slot);
        if (stack != null && stack.what() instanceof AEItemKey item) {
            return item.toStack((int) stack.amount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        var inserted = logic.getStorage().insert(slot, AEItemKey.of(stack), stack.getCount(), Actionable.ofSimulate(simulate));
        stack.shrink((int) inserted);
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!(logic.getStorage().getKey(slot) instanceof AEItemKey key)) {
            return ItemStack.EMPTY;
        }

        var extracted = logic.getStorage().extract(slot, key, amount, Actionable.ofSimulate(simulate));
        return key.toStack((int) extracted);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (!(logic.getStorage().getKey(slot) instanceof AEItemKey key)) {
            return 0;
        }

        return (int) logic.getStorage().getMaxAmount(key);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (!(logic.getStorage().getKey(slot) instanceof AEItemKey key)) {
            return false;
        }

        return ItemStack.isSameItemSameComponents(key.getReadOnlyStack(), stack);
    }
}
