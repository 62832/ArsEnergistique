package gripe._90.arseng.mixin;

import java.util.List;
import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.AECapabilities;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;

@Mixin(value = ScribesTile.class, remap = false)
public abstract class ScribesTileMixin extends ModdedTile {
    @Shadow
    public List<ItemStack> consumedStacks;

    public ScribesTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract boolean canConsumeItemstack(ItemStack stack);

    @Inject(method = "takeNearby", at = @At("TAIL"))
    private void takeFromInterfaces(CallbackInfo ci) {
        if (level == null) {
            return;
        }

        var area = BlockPos.betweenClosed(
                worldPosition.north(6).east(6).below(2),
                worldPosition.south(6).west(6).above(2));

        for (var pos : area) {
            var be = level.getBlockEntity(pos);

            if (be != null) {
                var hasExtracted = false;

                if (level.getCapability(AECapabilities.ME_STORAGE, pos, level.getBlockState(pos), be, null)
                        instanceof MEStorage storage) {
                    arseng$extract(storage, pos);
                    hasExtracted = true;
                }

                if (hasExtracted) {
                    return;
                }

                for (var side : Direction.values()) {
                    if (level.getCapability(AECapabilities.ME_STORAGE, pos, level.getBlockState(pos), be, side)
                            instanceof MEStorage storage) {
                        arseng$extract(storage, pos);
                        hasExtracted = true;
                    }

                    if (hasExtracted) {
                        return;
                    }
                }
            }
        }
    }

    @Unique
    private void arseng$extract(MEStorage storage, BlockPos pos) {
        for (var stored : storage.getAvailableStacks()) {
            if (stored.getKey() instanceof AEItemKey item && canConsumeItemstack(item.wrapForDisplayOrFilter())) {
                var extracted = storage.extract(item, 1, Actionable.MODULATE, IActionSource.empty());
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
