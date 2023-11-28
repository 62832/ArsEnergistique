package gripe._90.arseng.me.cell;

import net.minecraft.world.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.cells.ICellWorkbenchItem;

public interface ISourceCellItem extends ICellWorkbenchItem {
    long getTotalBytes();

    double getIdleDrain();

    @Override
    default boolean isEditable(ItemStack is) {
        return true;
    }

    @Override
    default FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    default void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {}
}
