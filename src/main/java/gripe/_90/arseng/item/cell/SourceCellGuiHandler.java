package gripe._90.arseng.item.cell;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.storage.cells.ICellGuiHandler;
import appeng.api.storage.cells.ICellHandler;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.common.MEStorageMenu;

public class SourceCellGuiHandler implements ICellGuiHandler {
    public static final SourceCellGuiHandler INSTANCE = new SourceCellGuiHandler();

    @Override
    public boolean isSpecializedFor(ItemStack cell) {
        return cell.getItem() instanceof SourceCellItem;
    }

    @Override
    public void openChestGui(Player player, IChestOrDrive chest, ICellHandler cellHandler, ItemStack cell) {
        chest.getUp();
        MenuOpener.open(MEStorageMenu.TYPE, player, MenuLocators.forBlockEntity((BlockEntity) chest));
    }
}
