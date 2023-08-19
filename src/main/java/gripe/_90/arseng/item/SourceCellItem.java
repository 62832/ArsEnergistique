package gripe._90.arseng.item;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.items.AEBaseItem;
import appeng.items.storage.StorageTier;

import gripe._90.arseng.me.cell.ISourceCellItem;

public class SourceCellItem extends AEBaseItem implements ISourceCellItem {
    private final StorageTier tier;

    public SourceCellItem(Properties properties, StorageTier tier) {
        super(properties);
        this.tier = tier;
    }

    public StorageTier getTier() {
        return tier;
    }

    @Override
    public long getTotalBytes() {
        return 1000 * (long) Math.pow(4, tier.index() - 1);
    }

    @Override
    public double getIdleDrain() {
        return tier.idleDrain();
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack is, Level level, @NotNull List<Component> lines, @NotNull TooltipFlag advancedTooltips) {
        addCellInformationToTooltip(is, lines);
    }
}
