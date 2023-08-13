package gripe._90.arseng.me.strategy;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.common.block.CreativeSourceJar;
import com.hollingsworth.arsnouveau.common.block.SourceJar;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

import gripe._90.arseng.me.key.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class SourceContainerItemStrategy implements ContainerItemStrategy<SourceKey, ItemStack> {
    public static final int MAX_SOURCE = 10000;

    private boolean isSourceJar(ItemStack stack) {
        return stack != null
                && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SourceJar;
    }

    private boolean isCreativeSourceJar(ItemStack stack) {
        return isSourceJar(stack) && ((BlockItem) stack.getItem()).getBlock() instanceof CreativeSourceJar;
    }

    private int getSource(ItemStack sourceJar) {
        Preconditions.checkArgument(isSourceJar(sourceJar));
        return isCreativeSourceJar(sourceJar)
                ? MAX_SOURCE
                : sourceJar.getOrCreateTag().getCompound("BlockEntityTag").getInt("source");
    }

    private void addSource(int source, ItemStack sourceJar) {
        Preconditions.checkArgument(isSourceJar(sourceJar));

        if (isCreativeSourceJar(sourceJar)) {
            return;
        }

        var beTag = sourceJar.getOrCreateTag().getCompound("BlockEntityTag");
        beTag.putInt("source", Math.min(MAX_SOURCE, getSource(sourceJar) + source));
        sourceJar.getOrCreateTag().put("BlockEntityTag", beTag);
    }

    @Nullable
    @Override
    public GenericStack getContainedStack(ItemStack stack) {
        return isSourceJar(stack) ? new GenericStack(SourceKey.KEY, getSource(stack)) : null;
    }

    @Nullable
    @Override
    public ItemStack findCarriedContext(Player player, AbstractContainerMenu menu) {
        var carried = menu.getCarried();
        return isSourceJar(carried) ? carried : null;
    }

    @Nullable
    @Override
    public ItemStack findPlayerSlotContext(Player player, int slot) {
        var carried = player.getInventory().getItem(slot);
        return isSourceJar(carried) ? carried : null;
    }

    @Override
    public long extract(ItemStack context, SourceKey what, long amount, Actionable mode) {
        var extracted = (int) Math.min(amount, getSource(context));

        if (extracted > 0 && mode == Actionable.MODULATE) {
            addSource(-extracted, context);
        }

        return extracted;
    }

    @Override
    public long insert(ItemStack context, SourceKey what, long amount, Actionable mode) {
        var inserted = (int) Math.min(amount, MAX_SOURCE - getSource(context));

        if (inserted > 0 && mode == Actionable.MODULATE) {
            addSource(inserted, context);
        }

        return inserted;
    }

    @Override
    public void playFillSound(Player player, SourceKey what) {}

    @Override
    public void playEmptySound(Player player, SourceKey what) {}

    @Nullable
    @Override
    public GenericStack getExtractableContent(ItemStack context) {
        return getContainedStack(context);
    }
}
