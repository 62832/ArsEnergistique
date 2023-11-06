package gripe._90.arseng.me.cell;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
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

    default void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        SourceCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }

    default Optional<TooltipComponent> getCellTooltipImage(ItemStack is) {
        Preconditions.checkArgument(is.getItem() == this);
        return SourceCellHandler.INSTANCE.getTooltipImage(is);
    }
}
