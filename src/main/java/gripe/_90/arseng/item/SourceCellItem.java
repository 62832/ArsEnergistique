package gripe._90.arseng.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import appeng.api.storage.StorageCells;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.PlayerMessages;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;

import gripe._90.arseng.me.cell.ISourceCellItem;
import gripe._90.arseng.me.cell.SourceCellHandler;

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
        return 100 * (long) Math.pow(4, tier.index() - 1);
    }

    @Override
    public double getIdleDrain() {
        return tier.idleDrain();
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, 1);
    }

    @ParametersAreNonnullByDefault
    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var inHand = player.getItemInHand(hand);
        disassemble(inHand, level, player);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), inHand);
    }

    @NotNull
    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        return disassemble(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }

    private boolean disassemble(ItemStack stack, Level level, Player player) {
        if (player != null && player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                return false;
            }

            var disassembledStacks = StorageCellDisassemblyRecipe.getDisassemblyResult(level, stack.getItem());

            if (disassembledStacks.isEmpty()) {
                return false;
            }

            var playerInv = player.getInventory();
            var cellInv = StorageCells.getCellInventory(stack, null);

            if (cellInv != null && playerInv.getSelected() == stack) {
                if (cellInv.getAvailableStacks().isEmpty()) {
                    playerInv.setItem(playerInv.selected, ItemStack.EMPTY);

                    for (var upgrade : getUpgrades(stack)) {
                        playerInv.placeItemBackInInventory(upgrade);
                    }

                    for (var disassembled : disassembledStacks) {
                        playerInv.placeItemBackInInventory(disassembled);
                    }

                    return true;
                } else {
                    player.displayClientMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text(), true);
                }
            }
        }

        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advTooltips) {
        SourceCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return SourceCellHandler.INSTANCE.getTooltipImage(stack);
    }
}
