package gripe._90.arseng.item.cell;

import java.util.List;

import com.google.common.base.Preconditions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.items.AEBaseItem;
import appeng.util.ConfigInventory;

public class SourceCellItem extends AEBaseItem implements ICellWorkbenchItem {
    public SourceCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEditable(ItemStack is) {
        return false;
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return null;
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

    @Override
    public void appendHoverText(ItemStack is, Level p_41422_, List<Component> lines, TooltipFlag advancedTooltips) {
        Preconditions.checkArgument(is.getItem() == this);
        SourceCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }
}
