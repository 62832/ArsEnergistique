package gripe._90.arseng.me.strategy;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;

import gripe._90.arseng.me.key.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class SourceStorageExportStrategy implements StackExportStrategy {
    private final BlockCapabilityCache<ISourceCap, Direction> cache;

    public SourceStorageExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        cache = BlockCapabilityCache.create(CapabilityRegistry.SOURCE_CAPABILITY, level, fromPos, fromSide);
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount) {
        if (!(what instanceof SourceKey)) {
            return 0;
        }

        var sourceTile = cache.getCapability();

        if (sourceTile != null) {
            var insertable = sourceTile.receiveSource(Ints.saturatedCast(amount), true);
            var extracted = (int) StorageHelper.poweredExtraction(
                    context.getEnergySource(),
                    context.getInternalStorage().getInventory(),
                    SourceKey.KEY,
                    insertable,
                    context.getActionSource(),
                    Actionable.MODULATE);

            if (extracted > 0 && sourceTile.canAcceptSource(extracted)) {
                sourceTile.receiveSource(extracted, false);
            }

            return extracted;
        }

        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        if (!(what instanceof SourceKey)) {
            return 0;
        }

        var sourceTile = cache.getCapability();
        return sourceTile != null ? sourceTile.receiveSource(Ints.saturatedCast(amount), mode.isSimulate()) : 0;
    }
}
