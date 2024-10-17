package gripe._90.arseng.me.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;

import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@SuppressWarnings("UnstableApiUsage")
public class SourceStorageImportStrategy implements StackImportStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceStorageImportStrategy.class);

    private final BlockCapabilityCache<ISourceCap, Direction> cache;

    public SourceStorageImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        cache = BlockCapabilityCache.create(CapabilityRegistry.SOURCE_CAPABILITY, level, fromPos, fromSide);
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(SourceKeyType.TYPE)) {
            return false;
        }

        var sourceTile = cache.getCapability();

        if (sourceTile == null) {
            return false;
        }

        int remainingTransferAmount = context.getOperationsRemaining() * SourceKeyType.TYPE.getAmountPerOperation();
        int rawAmount = Math.min(remainingTransferAmount, sourceTile.getSource());

        var inv = context.getInternalStorage().getInventory();

        // Check how much source we can actually insert
        var amount = inv.insert(SourceKey.KEY, rawAmount, Actionable.SIMULATE, context.getActionSource());

        if (amount > 0) {
            sourceTile.extractSource((int) amount, false);
        }

        var inserted = inv.insert(SourceKey.KEY, amount, Actionable.MODULATE, context.getActionSource());

        if (inserted < amount) {
            var leftover = amount - inserted;
            var backFill = (int) Math.min(leftover, sourceTile.getSourceCapacity() - sourceTile.getSource());

            if (backFill > 0) {
                sourceTile.receiveSource(backFill, false);
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
