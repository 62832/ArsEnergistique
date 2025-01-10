package gripe._90.arseng.mixin;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.patternprovider.PatternContainer;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = WixieCauldronTile.class, remap = false)
public abstract class WixieCauldronTileMixin extends SummoningTile {
    public WixieCauldronTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

@ModifyExpressionValue(method = "rotateCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), remap = false)
private BlockEntity checkBlockEntity(BlockEntity be, @Local List<ItemStack> craftables) {
    if (!(be instanceof PatternContainer networked)) return be;

    InternalInventory inv = networked.getTerminalPatternInventory();
    for (ItemStack stack : inv) {
        if (!PatternDetailsHelper.isEncodedPattern(stack)) continue;

        IPatternDetails details = PatternDetailsHelper.decodePattern(stack, be.getLevel());
        if (details == null) continue;
        for (GenericStack output : details.getOutputs()) {
            AEKey key = output.what();
            if (key instanceof AEItemKey itemKey) {
                craftables.add(itemKey.toStack(Math.toIntExact(output.amount())));
            }
        }
    }

    return be;
}
}
