package gripe._90.arseng.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.helpers.patternprovider.PatternContainer;

@Mixin(value = WixieCauldronTile.class, remap = false)
public abstract class WixieCauldronTileMixin extends SummoningTile {
    public WixieCauldronTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // spotless:off
    @ModifyExpressionValue(
            method = "rotateCraft",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    // spotless:on
    private BlockEntity checkBlockEntity(BlockEntity be, @Local List<ItemStack> craftables) {
        if (!(be instanceof PatternContainer patternContainer)) {
            return be;
        }

        for (var stack : patternContainer.getTerminalPatternInventory()) {
            if (!PatternDetailsHelper.isEncodedPattern(stack)) {
                continue;
            }

            if (PatternDetailsHelper.decodePattern(stack, be.getLevel()) instanceof IPatternDetails details) {
                for (var output : details.getOutputs()) {
                    if (output.what() instanceof AEItemKey itemKey) {
                        craftables.add(itemKey.toStack(Math.toIntExact(output.amount())));
                    }
                }
            }
        }

        return be;
    }
}
