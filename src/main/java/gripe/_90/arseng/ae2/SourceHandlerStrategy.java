package gripe._90.arseng.ae2;

import java.util.Set;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import org.jetbrains.annotations.Nullable;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.me.storage.ExternalStorageFacade;
import appeng.parts.automation.HandlerStrategy;

public class SourceHandlerStrategy extends HandlerStrategy<ISourceTile, Object> {

    public static final SourceHandlerStrategy INSTANCE = new SourceHandlerStrategy();

    public SourceHandlerStrategy() {
        super(SourceKeyType.TYPE);
    }

    @Override
    public ExternalStorageFacade getFacade(ISourceTile handler) {
        return new ExternalStorageFacade() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Nullable
            @Override
            public GenericStack getStackInSlot(int slot) {
                return new GenericStack(SourceKey.KEY, handler.getSource());
            }

            @Override
            public AEKeyType getKeyType() {
                return SourceKeyType.TYPE;
            }

            @Override
            protected int insertExternal(AEKey what, int amount, Actionable mode) {
                return Ints.saturatedCast(SourceHandlerStrategy.this.insert(handler, what, amount, mode));
            }

            @Override
            protected int extractExternal(AEKey what, int amount, Actionable mode) {
                if (!(what instanceof SourceKey)) {
                    return 0;
                }

                if (mode == Actionable.MODULATE) {
                    var initial = handler.getSource();
                    handler.removeSource(amount);
                    return Math.max(0, initial - handler.getSource());
                }

                return 0;
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

    @Nullable
    @Override
    public Object getStack(AEKey what, long amount) {
        return null;
    }

    @Override
    public long insert(ISourceTile handler, AEKey what, long amount, Actionable mode) {
        if (!(what instanceof SourceKey)) {
            return 0;
        }

        if (mode == Actionable.MODULATE) {
            var initial = handler.getSource();
            handler.addSource(Ints.saturatedCast(amount));
            return Math.max(0, handler.getSource() - initial);
        }

        var space = handler.getMaxSource() - handler.getSource();
        return Math.min(amount, space);
    }
}
