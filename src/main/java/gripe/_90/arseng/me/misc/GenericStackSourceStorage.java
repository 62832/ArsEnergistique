package gripe._90.arseng.me.misc;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;

import gripe._90.arseng.me.key.SourceKey;

@SuppressWarnings("UnstableApiUsage")
public record GenericStackSourceStorage(GenericInternalInventory inv) implements ISourceCap {
    public static void registerCapability(RegisterCapabilitiesEvent event) {
        for (var block : BuiltInRegistries.BLOCK) {
            if (event.isBlockRegistered(AECapabilities.GENERIC_INTERNAL_INV, block)) {
                event.registerBlock(
                        CapabilityRegistry.SOURCE_CAPABILITY,
                        (level, pos, state, be, context) -> {
                            var genericInv =
                                    level.getCapability(AECapabilities.GENERIC_INTERNAL_INV, pos, state, be, context);
                            return genericInv != null ? new GenericStackSourceStorage(genericInv) : null;
                        },
                        block);
            }
        }
    }

    @Override
    public int getMaxExtract() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxReceive() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canAcceptSource(int source) {
        return insert(1, Actionable.SIMULATE) > 0;
    }

    @Override
    public boolean canProvideSource(int source) {
        return extract(1, Actionable.SIMULATE) > 0;
    }

    @Override
    public int getSource() {
        return extract(Integer.MAX_VALUE, Actionable.SIMULATE);
    }

    @Override
    public int getSourceCapacity() {
        var slots = 0;

        for (var i = 0; i < inv.size(); i++) {
            var key = inv.getKey(i);

            if (key == null || key == SourceKey.KEY) {
                slots += 1;
            }
        }

        return Ints.saturatedCast(slots * inv.getMaxAmount(SourceKey.KEY));
    }

    @Override
    public int receiveSource(int source, boolean simulate) {
        return insert(source, Actionable.ofSimulate(simulate));
    }

    @Override
    public int extractSource(int source, boolean simulate) {
        return extract(source, Actionable.ofSimulate(simulate));
    }

    private int insert(int amount, Actionable mode) {
        var inserted = 0L;

        for (var i = 0; i < inv.size() && inserted < amount; ++i) {
            inserted += inv.insert(i, SourceKey.KEY, amount - inserted, mode);
        }

        return inserted > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) inserted;
    }

    private int extract(int amount, Actionable mode) {
        var extracted = 0L;

        for (var i = 0; i < inv.size() && extracted < amount; ++i) {
            extracted += inv.extract(i, SourceKey.KEY, amount - extracted, mode);
        }

        return extracted > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) extracted;
    }

    @Override
    public void setMaxSource(int max) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSource(int source) {
        throw new UnsupportedOperationException();
    }
}
