package gripe._90.arseng.mixin;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.IStorageMonitorableAccessor;
import appeng.capabilities.Capabilities;

import gripe._90.arseng.block.entity.PlayerAwareScribesTile;

@Mixin(value = ScribesTile.class, remap = false)
public abstract class ScribesTileMixin extends ModdedTile implements PlayerAwareScribesTile {
    @Shadow
    public List<ItemStack> consumedStacks;

    @Unique
    private Player arseng$player;

    public ScribesTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract boolean canConsumeItemstack(ItemStack stack);

    @Override
    public void arseng$setPlayer(Player player) {
        arseng$player = player;
    }

    @Inject(method = "setRecipe", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void setPlayer(GlyphRecipe recipe, Player player, CallbackInfo ci, ScribesTile tile) {
        ((PlayerAwareScribesTile) tile).arseng$setPlayer(player);
    }

    @Inject(method = "takeNearby", at = @At("TAIL"))
    private void takeFromInterfaces(CallbackInfo ci) {
        if (level == null || arseng$player == null) {
            return;
        }

        var area = BlockPos.betweenClosed(
                worldPosition.north(6).east(6).below(2),
                worldPosition.south(6).west(6).above(2));

        for (var pos : area) {
            var be = level.getBlockEntity(pos);

            if (be != null) {
                var hasExtracted = new AtomicBoolean(false);

                be.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR).ifPresent(storage -> {
                    arseng$extract(storage, pos);
                    hasExtracted.set(true);
                });

                if (hasExtracted.get()) {
                    return;
                }

                for (var side : Direction.values()) {
                    be.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR, side)
                            .ifPresent(storage -> {
                                arseng$extract(storage, pos);
                                hasExtracted.set(true);
                            });

                    if (hasExtracted.get()) {
                        return;
                    }
                }
            }
        }
    }

    @Unique
    private void arseng$extract(IStorageMonitorableAccessor monitorable, BlockPos pos) {
        if (arseng$player == null) {
            return;
        }

        var actionSource = IActionSource.ofPlayer(arseng$player);
        var storage = monitorable.getInventory(actionSource);

        if (storage != null) {
            for (var stored : storage.getAvailableStacks()) {
                if (stored.getKey() instanceof AEItemKey item && canConsumeItemstack(item.wrapForDisplayOrFilter())) {
                    var extracted = storage.extract(item, 1, Actionable.MODULATE, actionSource);
                    var taken = item.toStack((int) extracted);
                    consumedStacks.add(taken);

                    var flyingItem = new EntityFlyingItem(level, pos, getBlockPos());
                    flyingItem.setStack(taken);
                    Objects.requireNonNull(level).addFreshEntity(flyingItem);
                    updateBlock();
                    return;
                }
            }
        }
    }
}
