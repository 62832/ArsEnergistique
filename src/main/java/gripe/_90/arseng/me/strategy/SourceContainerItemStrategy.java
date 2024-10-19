package gripe._90.arseng.me.strategy;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.hollingsworth.arsnouveau.common.block.CreativeSourceJar;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

import gripe._90.arseng.me.key.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class SourceContainerItemStrategy
        implements ContainerItemStrategy<SourceKey, SourceContainerItemStrategy.Context> {

    public static final SourceContainerItemStrategy INSTANCE = new SourceContainerItemStrategy();

    private SourceContainerItemStrategy() {}

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
        return isCreativeSourceJar(sourceJar) ? SourceKey.MAX_SOURCE : BlockFillContents.get(sourceJar);
    }

    private void changeSource(int amount, ItemStack sourceJar) {
        Preconditions.checkArgument(isSourceJar(sourceJar));

        if (isCreativeSourceJar(sourceJar)) {
            return;
        }

        sourceJar.set(
                DataComponentRegistry.BLOCK_FILL_CONTENTS,
                new BlockFillContents(Math.min(SourceKey.MAX_SOURCE, Math.max(getSource(sourceJar) + amount, 0))));
    }

    @Nullable
    @Override
    public GenericStack getContainedStack(ItemStack stack) {
        return isSourceJar(stack) ? new GenericStack(SourceKey.KEY, getSource(stack)) : null;
    }

    @Nullable
    @Override
    public Context findCarriedContext(Player player, AbstractContainerMenu menu) {
        var carried = menu.getCarried();
        return isSourceJar(carried) ? new CarriedContext(player, menu) : null;
    }

    @Nullable
    @Override
    public Context findPlayerSlotContext(Player player, int slot) {
        var carried = player.getInventory().getItem(slot);
        return isSourceJar(carried) ? new PlayerInvContext(player, slot) : null;
    }

    @Override
    public long extract(Context context, SourceKey what, long amount, Actionable mode) {
        var stackCopy = context.getStack().copy();
        stackCopy.setCount(1);

        var extracted = (int) Math.min(amount, getSource(stackCopy));

        if (extracted > 0 && mode == Actionable.MODULATE) {
            changeSource(-extracted, stackCopy);
            // remove emptied item from stack
            context.getStack().shrink(1);
            // add new emptied jar to context
            context.addOverflow(stackCopy);
        }

        return extracted;
    }

    @Override
    public long insert(Context context, SourceKey what, long amount, Actionable mode) {
        var stackCopy = context.getStack().copy();
        stackCopy.setCount(1);
        var inserted = (int) Math.min(amount, SourceKey.MAX_SOURCE - getSource(stackCopy));

        if (inserted > 0 && mode == Actionable.MODULATE) {
            changeSource(inserted, stackCopy);
            // remove filled item from stack
            context.getStack().shrink(1);
            // add new filled jar to context
            context.addOverflow(stackCopy);
        }

        return inserted;
    }

    @Override
    public void playFillSound(Player player, SourceKey what) {}

    @Override
    public void playEmptySound(Player player, SourceKey what) {}

    @Nullable
    @Override
    public GenericStack getExtractableContent(Context context) {
        return getContainedStack(context.getStack());
    }

    public interface Context {
        ItemStack getStack();

        void addOverflow(ItemStack stack);
    }

    private record CarriedContext(Player player, AbstractContainerMenu menu) implements Context {
        @Override
        public ItemStack getStack() {
            return menu.getCarried();
        }

        public void addOverflow(ItemStack stack) {
            if (menu.getCarried().isEmpty()) {
                menu.setCarried(stack);
            } else {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    private record PlayerInvContext(Player player, int slot) implements Context {
        @Override
        public ItemStack getStack() {
            return player.getInventory().getItem(slot);
        }

        public void addOverflow(ItemStack stack) {
            player.getInventory().placeItemBackInInventory(stack);
        }
    }
}
