package gripe._90.arseng.ae2.stack;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

import gripe._90.arseng.ae2.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class SourceContainerItemStrategy
        implements ContainerItemStrategy<SourceKey, SourceContainerItemStrategy.Context> {

    // TODO: Source Bucket is planned for removal in Ars, keep this in mind
    // This is also really fucking buggy as it stands

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        if (stack.is(ItemsRegistry.BUCKET_OF_SOURCE)) {
            return new GenericStack(SourceKey.KEY, 1000);
        }
        if (stack.is(Items.BUCKET)) {
            return new GenericStack(SourceKey.KEY, 0);
        }
        return null;
    }

    @Override
    public @Nullable Context findCarriedContext(Player player, AbstractContainerMenu menu) {
        return menu.getCarried().is(ItemsRegistry.BUCKET_OF_SOURCE) || menu.getCarried().is(Items.BUCKET)
                ? new Context(player, menu)
                : null;
    }

    @Override
    public long extract(Context context, SourceKey what, long amount, Actionable mode) {
        if (!context.menu.getCarried().is(ItemsRegistry.BUCKET_OF_SOURCE)) {
            return 0;
        }

        if (mode == Actionable.MODULATE) {
            context.menu.setCarried(new ItemStack(Items.BUCKET));
        }

        return 1000;
    }

    @Override
    public long insert(Context context, SourceKey what, long amount, Actionable mode) {
        if (!context.menu.getCarried().is(Items.BUCKET)) {
            return 0;
        }

        if (mode == Actionable.MODULATE) {
            context.menu.setCarried(new ItemStack(ItemsRegistry.BUCKET_OF_SOURCE));
        }

        return 1000;
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
