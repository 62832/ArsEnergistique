package gripe._90.arseng.me.strategy;

import org.jetbrains.annotations.Nullable;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;
import appeng.util.BlockApiCache;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@SuppressWarnings("UnstableApiUsage")
public class SourceExternalStorageStrategy implements ExternalStorageStrategy {
    private final BlockApiCache<IAdvancedSourceTile> apiCache;
    private final Direction fromSide;

    public SourceExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.apiCache = BlockApiCache.create(ArsEngCapabilities.SOURCE_TILE, level, fromPos);
        this.fromSide = fromSide;
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var sourceTile = apiCache.find(fromSide);
        return sourceTile != null ? new SourceStorageAdapter(sourceTile, injectOrExtractCallback) : null;
    }

    private record SourceStorageAdapter(ISourceTile sourceTile, Runnable injectOrExtractCallback) implements MEStorage {
        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource actionSource) {
            if (!(what instanceof SourceKey)) {
                return 0;
            }

            var inserted = (int) Math.min(amount, sourceTile.getMaxSource() - sourceTile.getSource());

            if (inserted > 0 && mode == Actionable.MODULATE) {
                sourceTile.addSource(inserted);
                injectOrExtractCallback.run();
            }

            return inserted;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource actionSource) {
            if (!(what instanceof SourceKey)) {
                return 0;
            }

            var extracted = (int) Math.min(amount, sourceTile.getSource());

            if (extracted > 0 && mode == Actionable.MODULATE) {
                sourceTile.removeSource(extracted);
                injectOrExtractCallback.run();
            }

            return extracted;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            var currentSource = sourceTile.getSource();

            if (currentSource != 0) {
                out.add(SourceKey.KEY, currentSource);
            }
        }

        @Override
        public Component getDescription() {
            return GuiText.ExternalStorage.text(SourceKeyType.TYPE.getDescription());
        }
    }
}
