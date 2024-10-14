package gripe._90.arseng.me.cell;

import net.minecraft.world.item.ItemStack;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

import gripe._90.arseng.item.CreativeSourceCellItem;

public class CreativeSourceCellHandler implements ICellHandler {
    public static final CreativeSourceCellHandler INSTANCE = new CreativeSourceCellHandler();

    private CreativeSourceCellHandler() {}

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof CreativeSourceCellItem;
    }

    @Override
    public StorageCell getCellInventory(ItemStack is, ISaveProvider host) {
        return isCell(is) ? new CreativeSourceCellInventory(is) : null;
    }
}
