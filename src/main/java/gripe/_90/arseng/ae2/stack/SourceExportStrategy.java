package gripe._90.arseng.ae2.stack;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;

import gripe._90.arseng.ae2.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public class SourceExportStrategy implements StackExportStrategy {

    private final ServerLevel level;
    private final BlockPos fromPos;

    public SourceExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount, Actionable mode) {
        if (!(what instanceof SourceKey)) {
            return 0;
        }

        var be = this.level.getBlockEntity(this.fromPos);
        if (!(be instanceof ISourceTile tile)) {
            return 0;
        }

        var inv = context.getInternalStorage();
        var extracted = StorageHelper.poweredExtraction(context.getEnergySource(), inv.getInventory(), what, amount,
                context.getActionSource(), Actionable.SIMULATE);

        var wasInserted = tile.addSource((int) extracted) - extracted;
        if (wasInserted > 0) {
            if (mode == Actionable.MODULATE) {
                StorageHelper.poweredExtraction(context.getEnergySource(), inv.getInventory(), what, wasInserted,
                        context.getActionSource(), Actionable.MODULATE);
            }
            return wasInserted;
        }

        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        if (!(what instanceof SourceKey)) {
            return 0;
        }

        var be = this.level.getBlockEntity(this.fromPos);
        if (!(be instanceof ISourceTile tile)) {
            return 0;
        }

        return tile.addSource((int) amount) - amount;
    }
}
