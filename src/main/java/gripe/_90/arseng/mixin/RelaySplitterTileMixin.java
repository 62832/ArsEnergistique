package gripe._90.arseng.mixin;

import java.util.ArrayList;
import java.util.Objects;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;

@Mixin(value = RelaySplitterTile.class, remap = false)
public abstract class RelaySplitterTileMixin extends AbstractSourceMachine {
    @Shadow
    ArrayList<BlockPos> toList;

    @Shadow
    ArrayList<BlockPos> fromList;

    public RelaySplitterTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Shadow
    public abstract void createParticles(BlockPos from, BlockPos to);

    @Inject(method = "processFromList", at = @At("HEAD"), cancellable = true)
    void addCapToProcessFrom(CallbackInfo ci) {
        ci.cancel();
        arseng$processStaleBlockPositions(fromList, false);
    }

    @Inject(method = "processToList", at = @At("HEAD"), cancellable = true)
    void addCapToProcessTo(CallbackInfo ci) {
        ci.cancel();
        arseng$processStaleBlockPositions(toList, true);
    }

    @Unique
    private void arseng$processStaleBlockPositions(ArrayList<BlockPos> posList, boolean sendSource) {
        if (posList.isEmpty()) return;

        var stale = new ArrayList<BlockPos>();
        var ratePer = getTransferRate() / posList.size();

        for (var pos : posList) {
            if (!Objects.requireNonNull(level).isLoaded(pos)) continue;

            var be = level.getBlockEntity(pos);

            if (be == null) {
                stale.add(pos);
                continue;
            }

            var cap = be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(), pos))
                    .resolve();

            if (cap.isPresent()) {
                ISourceTile fromTile = sendSource ? this : cap.get();
                ISourceTile toTile = sendSource ? cap.get() : this;
                if (transferSource(fromTile, toTile, ratePer) > 0) {
                    BlockPos fromPos = sendSource ? worldPosition : pos;
                    BlockPos toPos = sendSource ? pos : worldPosition;
                    createParticles(fromPos, toPos);
                }
            } else {
                stale.add(pos);
            }
        }

        for (var pos : stale) {
            posList.remove(pos);
            updateBlock();
        }
    }
}
