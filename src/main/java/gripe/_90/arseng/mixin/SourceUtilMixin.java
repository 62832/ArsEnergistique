package gripe._90.arseng.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;

@Mixin(value = SourceUtil.class, remap = false)
public abstract class SourceUtilMixin {
    @Inject(method = "canGiveSource", at = @At("RETURN"))
    private static void addCapToGive(
            BlockPos pos, Level world, int range, CallbackInfoReturnable<List<ISpecialSourceProvider>> cir) {
        var posList = cir.getReturnValue();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.isLoaded(b)) {
                var be = world.getBlockEntity(b);

                if (be != null) {
                    be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(pos, b))
                            .ifPresent(sourceTile -> {
                                if (sourceTile.canAcceptSource() && sourceTile.sourcelinksCanProvidePower()) {
                                    var provider = new SourceProvider(sourceTile, b.immutable());

                                    // account for the fact that posList isn't a Set and that source jars have the
                                    // capability attached to them but will have already been added as providers
                                    if (!posList.contains(provider)) {
                                        posList.add(provider);
                                    }
                                }
                            });
                }
            }
        });
    }

    @Inject(method = "canTakeSource", at = @At("RETURN"))
    private static void addCapToTake(
            BlockPos pos, Level world, int range, CallbackInfoReturnable<List<ISpecialSourceProvider>> cir) {
        var posList = cir.getReturnValue();
        BlockPos.withinManhattanStream(pos, range, range, range).forEach(b -> {
            if (world.isLoaded(b)) {
                var be = world.getBlockEntity(b);

                if (be != null) {
                    be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(pos, b))
                            .ifPresent(sourceTile -> {
                                if (sourceTile.getSource() > 0 && sourceTile.relayCanTakePower()) {
                                    var provider = new SourceProvider(sourceTile, b.immutable());

                                    if (!posList.contains(provider)) {
                                        posList.add(provider);
                                    }
                                }
                            });
                }
            }
        });
    }

    @Inject(method = "hasSourceNearby", at = @At("HEAD"), cancellable = true)
    private static void addCapToNearby(
            BlockPos pos, Level world, int range, int source, CallbackInfoReturnable<Boolean> cir) {
        var nearby = BlockPos.findClosestMatch(pos, range, range, (p) -> {
            var be = world.getBlockEntity(p);

            return be != null
                    && be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(pos, p))
                            .filter(sourceTile -> sourceTile.getSource() >= source && sourceTile.relayCanTakePower())
                            .isPresent();
        });

        if (nearby.isPresent()) {
            cir.setReturnValue(true);
        }
    }
}
