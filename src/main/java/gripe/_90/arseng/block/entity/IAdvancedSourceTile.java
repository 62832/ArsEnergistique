package gripe._90.arseng.block.entity;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IAdvancedSourceTile extends ISourceTile {
    boolean relayCanTakePower();

    boolean sourcelinksCanProvidePower();

    static Direction getDirTo(BlockPos from, BlockPos to) {
        var x = from.getX() - to.getX();
        var y = from.getY() - to.getY();
        var z = from.getZ() - to.getZ();

        if (Math.abs(y) >= Math.abs(x) && Math.abs(y) >= Math.abs(z)) {
            return y > 0 ? Direction.UP : Direction.DOWN;
        } else if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }
}
