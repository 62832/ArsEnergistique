package gripe._90.arseng.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.upgrades.Upgrades;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.menu.me.common.MEStorageMenu;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.cell.ISourceCellItem;
import gripe._90.arseng.me.cell.SourceCellHandler;

public class PortableSourceCellItem extends AbstractPortableCell implements ISourceCellItem {
    private final StorageTier tier;

    public PortableSourceCellItem(Properties props, StorageTier tier) {
        super(MEStorageMenu.PORTABLE_FLUID_CELL_TYPE, props, 0xFFFFFF);
        this.tier = tier;
    }

    public StorageTier getTier() {
        return tier;
    }

    @Override
    public long getTotalBytes() {
        return 50 * (long) Math.pow(4, tier.index() - 1);
    }

    @Override
    public double getIdleDrain() {
        return tier.idleDrain();
    }

    @Override
    public ResourceLocation getRecipeId() {
        return ArsEnergistique.makeId(Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, level, lines, advancedTooltips);
        SourceCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return SourceCellHandler.INSTANCE.getTooltipImage(stack);
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 80D * (Upgrades.getEnergyCardMultiplier(getUpgrades(stack)) + 1);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 3, this::onUpgradesChanged);
    }

    public static void initColours(RegisterColorHandlersEvent.Item event) {
        ArsEngItems.getPortables().forEach(portable -> event.register(PortableSourceCellItem::getColor, portable));
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1 && stack.getItem() instanceof PortableSourceCellItem sourceCell) {
            if (sourceCell.getAECurrentPower(stack) <= 0) {
                return CellState.ABSENT.getStateColor();
            }

            var cellInv = StorageCells.getCellInventory(stack, null);
            var cellStatus = cellInv != null ? cellInv.getStatus() : CellState.EMPTY;
            return cellStatus.getStateColor();
        }

        // we're only concerned about the LED colour and don't care about dyeing these cells
        return 0xFFFFFF;
    }
}
