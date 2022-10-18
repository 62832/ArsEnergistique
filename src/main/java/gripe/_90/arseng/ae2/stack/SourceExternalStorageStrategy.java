package gripe._90.arseng.ae2.stack;

import java.util.Set;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.me.storage.ExternalStorageFacade;

import gripe._90.arseng.ae2.SourceKey;
import gripe._90.arseng.ae2.SourceKeyType;

@SuppressWarnings("UnstableApiUsage")
public class SourceExternalStorageStrategy implements ExternalStorageStrategy {

    private final ServerLevel level;
    private final BlockPos fromPos;

    public SourceExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var sourceStorage = this.level.getBlockEntity(this.fromPos);

        if (!(sourceStorage instanceof ISourceTile tile)) {
            return null;
        }

        var result = getFacade(tile);
        result.setChangeListener(injectOrExtractCallback);
        result.setExtractableOnly(extractableOnly);
        return result;
    }

    protected static ExternalStorageFacade getFacade(ISourceTile tile) {
        return new ExternalStorageFacade() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public GenericStack getStackInSlot(int slot) {
                return tile.getSource() > 0 ? new GenericStack(SourceKey.KEY, tile.getSource()) : null;
            }

            @Override
            public AEKeyType getKeyType() {
                return SourceKeyType.TYPE;
            }

            @Override
            public void getAvailableStacks(KeyCounter out) {
                if (tile.getSource() > 0) {
                    out.add(SourceKey.KEY, tile.getSource());
                }
            }

            @Override
            protected int insertExternal(AEKey what, int amount, Actionable mode) {
                if (what instanceof SourceKey) {
                    return tile.addSource(amount) - amount;
                }

                return 0;
            }

            @Override
            protected int extractExternal(AEKey what, int amount, Actionable mode) {
                if (!(what instanceof SourceKey)) {
                    return 0;
                }

                if (mode == Actionable.MODULATE) {
                    var before = tile.getSource();
                    tile.removeSource(amount);
                    return Math.max(0, before - tile.getSource());
                }

                return Math.min(amount, tile.getSource());
            }

            @Override
            public boolean containsAnyFuzzy(Set<AEKey> keys) {
                for (var key : keys) {
                    if (key instanceof SourceKey) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
