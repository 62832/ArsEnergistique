package gripe._90.arseng.me.stack;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;

import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@SuppressWarnings("UnstableApiUsage")
public class SourceImportStrategy implements StackImportStrategy {

    private final ServerLevel level;
    private final BlockPos fromPos;

    public SourceImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(SourceKeyType.TYPE)) {
            return false;
        }

        var be = this.level.getBlockEntity(this.fromPos);
        if (!(be instanceof ISourceTile tile)) {
            return false;
        }

        var inv = context.getInternalStorage();

        if (!context.isInFilter(SourceKey.KEY)) {
            return false;
        }

        // TODO: This is still kinda buggy

        // Check how much source we can actually insert into the network (might be 0 if all available cells are full)
        var amountForSource = inv.getInventory().insert(SourceKey.KEY,
                context.getOperationsRemaining(),
                Actionable.SIMULATE, context.getActionSource());

        // Try to simulate-extract it
        var amount = tile.getSource() - Ints.saturatedCast(amountForSource);
        if (amount > 0) {
            var inserted = inv.getInventory().insert(SourceKey.KEY, amount, Actionable.MODULATE,
                    context.getActionSource());
            tile.removeSource(amount);

            var opsUsed = Math.max(1, inserted);
            context.reduceOperationsRemaining(opsUsed);
        }

        return false;
    }
}
