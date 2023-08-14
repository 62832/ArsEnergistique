package gripe._90.arseng.me.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.util.BlockApiCache;

import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@SuppressWarnings("UnstableApiUsage")
public class SourceStorageImportStrategy implements StackImportStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceStorageImportStrategy.class);

    private final BlockApiCache<ISourceTile> apiCache;
    private final Direction fromSide;

    public SourceStorageImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ArsEngCapabilities.SOURCE_TILE, level, fromPos);
        this.fromSide = fromSide;
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(SourceKeyType.TYPE)) {
            return false;
        }

        var sourceTile = apiCache.find(fromSide);

        if (sourceTile == null) {
            return false;
        }

        var remainingTransferAmount =
                (long) context.getOperationsRemaining() * SourceKeyType.TYPE.getAmountPerOperation();
        var amount = (int) Math.min(remainingTransferAmount, sourceTile.getSource());

        if (amount > 0) {
            sourceTile.removeSource(amount);
        }

        var inv = context.getInternalStorage();
        var inserted = inv.getInventory().insert(SourceKey.KEY, amount, Actionable.MODULATE, context.getActionSource());

        if (inserted < amount) {
            var leftover = amount - inserted;
            var backFill = (int) Math.min(leftover, sourceTile.getMaxSource() - sourceTile.getSource());

            if (backFill > 0) {
                sourceTile.addSource(backFill);
            }

            if (leftover > backFill) {
                LOGGER.error("Storage import issue, voided {} source.", leftover - backFill);
            }
        }

        var opsUsed = Math.max(1, inserted / SourceKeyType.TYPE.getAmountPerOperation());
        context.reduceOperationsRemaining(opsUsed);

        return amount > 0;
    }
}
