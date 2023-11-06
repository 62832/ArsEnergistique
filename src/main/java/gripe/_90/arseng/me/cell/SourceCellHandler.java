package gripe._90.arseng.me.cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageCellTooltipComponent;

import gripe._90.arseng.definition.ArsEngItems;

public class SourceCellHandler implements ICellHandler {
    public static final SourceCellHandler INSTANCE = new SourceCellHandler();

    private SourceCellHandler() {}

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof ISourceCellItem;
    }

    @Nullable
    @Override
    public SourceCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
        return isCell(is) ? new SourceCellInventory((ISourceCellItem) is.getItem(), is, container) : null;
    }

    public static void initLED(RegisterColorHandlersEvent.Item event) {
        ArsEngItems.getCells().forEach(cell -> event.register(BasicStorageCell::getColor, cell));
    }

    void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);

        if (handler != null) {
            lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
        }
    }

    Optional<TooltipComponent> getTooltipImage(ItemStack is) {
        var handler = getCellInventory(is, null);
        if (handler == null) return Optional.empty();

        var upgrades = new ArrayList<ItemStack>();

        if (AEConfig.instance().isTooltipShowCellUpgrades()) {
            handler.getUpgrades().forEach(upgrades::add);
        }

        return Optional.of(new StorageCellTooltipComponent(upgrades, Collections.emptyList(), false, false));
    }
}
