package gripe._90.arseng.me.cell;

import java.util.List;

import com.google.common.base.Preconditions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.util.ConfigInventory;

public interface ISourceCellItem extends ICellWorkbenchItem {
    long getTotalBytes();

    double getIdleDrain();

    @Override
    default boolean isEditable(ItemStack is) {
        return false;
    }

    @Override
    default ConfigInventory getConfigInventory(ItemStack is) {
        return null;
    }

    @Override
    default FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    default void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {}

    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        SourceCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }
}
