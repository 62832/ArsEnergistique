package gripe._90.arseng.me.cell;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.items.storage.BasicStorageCell;

import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.arseng.me.key.SourceKey;

public class SourceCellHandler implements ICellHandler {
    public static final SourceCellHandler INSTANCE = new SourceCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return is != null && is.getItem() instanceof SourceCellItem;
    }

    @Nullable
    @Override
    public StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider container) {
        if (!is.isEmpty() && is.getItem() instanceof SourceCellItem) {
            return new SourceCellInventory(is, container);
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static void initLED(RegisterColorHandlersEvent.Item event) {
        event.register(BasicStorageCell::getColor, ArsEngItems.SOURCE_STORAGE_CELL);
    }

    @OnlyIn(Dist.CLIENT)
    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        var source = handler.getAvailableStacks().get(SourceKey.KEY);
        lines.add(Tooltips.of(
                Tooltips.ofUnformattedNumberWithRatioColor(
                        source, (double) source / SourceCellInventory.MAX_SOURCE, false),
                Tooltips.of(" "),
                Tooltips.of(GuiText.Of),
                Tooltips.of(" "),
                Tooltips.ofUnformattedNumber(SourceCellInventory.MAX_SOURCE),
                Tooltips.of(" Source Stored")));
    }
}
