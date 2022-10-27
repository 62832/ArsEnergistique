package gripe._90.arseng.mixin;

import java.util.List;
import java.util.Optional;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceManager;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import gripe._90.arseng.capability.ArsEngCapabilities;

@Mixin(value = SourceUtil.class, remap = false)
public class SourceUtilMixin {

    @Inject(method = { "lambda$canGiveSource$0", "lambda$canTakeSource$1" }, at = @At("HEAD"))
    private static void inject1(Level world, List<ISpecialSourceProvider> posList, BlockPos b, CallbackInfo ci) {
        if (world.isLoaded(b)) {
            var be = world.getBlockEntity(b);
            if (be != null) {
                be.getCapability(ArsEngCapabilities.SOURCE_PROVIDER)
                        .ifPresent(tile -> posList.add(new SourceProvider(tile, b.immutable())));
            }
        }
    }

    @Inject(method = "hasSourceNearby", at = @At("HEAD"), cancellable = true)
    private static void inject2(BlockPos pos, Level world, int range, int mana, CallbackInfoReturnable<Boolean> cir) {
        Optional<BlockPos> loc = BlockPos.findClosestMatch(pos, range, range, (b) -> {
            var be = world.getBlockEntity(b);
            if (be != null) {
                ISourceTile tile = be.getCapability(ArsEngCapabilities.SOURCE_PROVIDER).orElse(null);
                return (tile != null && tile.getSource() >= mana);
            }
            return false;
        });
        if (loc.isPresent()) {
            cir.setReturnValue(true);
        }
        cir.setReturnValue(SourceManager.INSTANCE.hasSourceNearby(pos, world, range, mana) != null);
    }
}
