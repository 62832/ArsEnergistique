package gripe._90.arseng.me.strategy;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;

import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.key.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class SourceStorageExportStrategy implements StackExportStrategy {
    private final BlockApiCache<ISourceTile> apiCache;
    private final Direction fromSide;

    public SourceStorageExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ArsEngCapabilities.SOURCE_TILE, level, fromPos);
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount) {
        if (!(what instanceof SourceKey)) {
            return 0;
        }

        var sourceTile = apiCache.find(fromSide);

        if (sourceTile != null) {
            var insertable = (int) Math.min(amount, sourceTile.getMaxSource() - sourceTile.getSource());
            var extracted = (int) StorageHelper.poweredExtraction(
                    context.getEnergySource(),
                    context.getInternalStorage().getInventory(),
                    SourceKey.KEY,
                    insertable,
                    context.getActionSource(),
                    Actionable.MODULATE);

            if (extracted > 0) {
                sourceTile.addSource(extracted);
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

        var sourceTile = apiCache.find(fromSide);

        if (sourceTile != null) {
            var inserted = (int) Math.min(amount, sourceTile.getMaxSource() - sourceTile.getSource());

            if (inserted > 0 && mode == Actionable.MODULATE) {
                sourceTile.addSource(inserted);
            }

            return inserted;
        }

        return 0;
    }
}
