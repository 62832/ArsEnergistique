package gripe._90.arseng.me.cell;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.core.localization.Tooltips;

import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.arseng.me.key.SourceKey;

public class SourceCellHandler implements ICellHandler {
    public static final SourceCellHandler INSTANCE = new SourceCellHandler();

    public static void register(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> StorageCells.addCellHandler(INSTANCE));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> StorageCellModels.registerModel(
                ArsEngItems.SOURCE_STORAGE_CELL, ArsEngCore.makeId("block/drive/cells/source_storage_cell")));
    }

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

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        var source = handler.getAvailableStacks().get(SourceKey.KEY);
        lines.add(Tooltips.of(
                Tooltips.ofUnformattedNumberWithRatioColor(
                        source, (double) source / SourceCellInventory.MAX_SOURCE, false),
                Tooltips.of(" of "),
                Tooltips.ofUnformattedNumber(SourceCellInventory.MAX_SOURCE),
                Tooltips.of(" Source Stored")));
    }
}
