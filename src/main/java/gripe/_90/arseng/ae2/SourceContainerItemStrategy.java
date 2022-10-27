package gripe._90.arseng.ae2;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

@SuppressWarnings("UnstableApiUsage")
public class SourceContainerItemStrategy
        implements ContainerItemStrategy<SourceKey, SourceContainerItemStrategy.Context> {

    private boolean isSourceJar(ItemStack stack) {
        return stack != null && stack.getItem()instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SourceJar;
    }

    private int getSource(ItemStack stack) {
        if (isSourceJar(stack) && stack.getTag() != null && stack.getTag().contains("BlockEntityTag")) {
            return stack.getTag().getCompound("BlockEntityTag").getInt("source");
        }
        return 0;
    }

    private void setSource(ItemStack stack, int source) {
        if (isSourceJar(stack)) {
            var tileTag = new CompoundTag();
            tileTag.putInt("source", source);
            stack.getOrCreateTag().put("BlockEntityTag", tileTag);
        }
    }

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        return isSourceJar(stack) ? new GenericStack(SourceKey.KEY, getSource(stack)) : null;
    }

    @Override
    public @Nullable Context findCarriedContext(Player player, AbstractContainerMenu menu) {
        return isSourceJar(menu.getCarried()) ? new Context(player, menu) : null;
    }

    @Override
    public long extract(@NotNull Context context, SourceKey what, long amount, Actionable mode) {
        var held = context.menu.getCarried();
        var copy = held.copy();

        if (!isSourceJar(copy)) {
            return 0;
        }

        var before = getSource(copy);
        setSource(copy, Math.max(0, before - Ints.saturatedCast(amount)));

        if (mode == Actionable.MODULATE) {
            held.shrink(1);

            if (held.isEmpty()) {
                context.menu.setCarried(copy);
            } else {
                context.player.getInventory().placeItemBackInInventory(copy);
            }
        }

        if (copy.getItem() == BlockRegistry.CREATIVE_SOURCE_JAR.asItem()) {
            return amount;
        } else {
            return before - getSource(copy);
        }
    }

    @Override
    public long insert(Context context, SourceKey what, long amount, Actionable mode) {
        var held = context.menu.getCarried();
        var copy = held.copy();

        if (!isSourceJar(copy)) {
            return 0;
        }

        var before = getSource(copy);
        setSource(copy, Math.max(0, before + Ints.saturatedCast(amount)));

        if (mode == Actionable.MODULATE) {
            held.shrink(1);

            if (held.isEmpty()) {
                context.menu.setCarried(copy);
            } else {
                context.player.getInventory().placeItemBackInInventory(copy);
            }
        }

        return getSource(copy) - before;
    }

    @Override
    public void playFillSound(Player player, SourceKey what) {
    }

    @Override
    public void playEmptySound(Player player, SourceKey what) {
    }

    @Override
    public @Nullable GenericStack getExtractableContent(Context context) {
        return getContainedStack(context.menu.getCarried());
    }

    record Context(Player player, AbstractContainerMenu menu) {
    }
}
