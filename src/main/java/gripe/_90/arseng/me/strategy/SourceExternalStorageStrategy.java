package gripe._90.arseng.me.strategy;

import org.jetbrains.annotations.Nullable;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;

import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@SuppressWarnings("UnstableApiUsage")
public class SourceExternalStorageStrategy implements ExternalStorageStrategy {
    private final BlockCapabilityCache<ISourceCap, Direction> cache;

    public SourceExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        cache = BlockCapabilityCache.create(CapabilityRegistry.SOURCE_CAPABILITY, level, fromPos, fromSide);
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var sourceTile = cache.getCapability();
        return sourceTile != null ? new Adaptor(sourceTile, injectOrExtractCallback) : null;
    }

    private record Adaptor(ISourceCap sourceTile, Runnable injectOrExtractCallback) implements MEStorage {
        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource actionSource) {
            if (!(what instanceof SourceKey)) {
                return 0;
            }

            var inserted = sourceTile.receiveSource(Ints.saturatedCast(amount), mode.isSimulate());

            if (inserted > 0 && mode == Actionable.MODULATE) {
                injectOrExtractCallback.run();
            }

            return inserted;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource actionSource) {
            if (!(what instanceof SourceKey)) {
                return 0;
            }

            var extracted = sourceTile.extractSource(Ints.saturatedCast(amount), mode.isSimulate());

            if (extracted > 0 && mode == Actionable.MODULATE) {
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
