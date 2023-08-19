package gripe._90.arseng.definition;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import appeng.capabilities.Capabilities;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.block.entity.SourceTileWrapper;
import gripe._90.arseng.me.storage.GenericStackSourceStorage;

public final class ArsEngCapabilities {
    private ArsEngCapabilities() {}

    public static final Capability<IAdvancedSourceTile> SOURCE_TILE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ISourceTile.class);
    }

    public static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        var be = event.getObject();

        if (be instanceof ISourceTile sourceTile) {
            var provider = new ICapabilityProvider() {
                private final LazyOptional<IAdvancedSourceTile> sourceHandler = LazyOptional.of(
                        //to semi-preserve the relay's behavior of only working on abstract source machines
                        //to reduce the risk of crashes with another addon
                        () -> new SourceTileWrapper(sourceTile, sourceTile instanceof AbstractSourceMachine, sourceTile instanceof SourceJarTile));

                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    return SOURCE_TILE.orEmpty(cap, sourceHandler);
                }

                private void invalidate() {
                    sourceHandler.invalidate();
                }
            };

            event.addCapability(ArsEngCore.makeId("source_tile"), provider);
            event.addListener(provider::invalidate);
        }

        var genericInvProvider = new ICapabilityProvider() {
            private LazyOptional<GenericStackSourceStorage> sourceHandler = LazyOptional.empty();

            @SuppressWarnings("UnstableApiUsage")
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == SOURCE_TILE) {
                    sourceHandler = be.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(GenericStackSourceStorage::new);
                }

                return sourceHandler.cast();
            }

            private void invalidate() {
                sourceHandler.invalidate();
            }
        };

        event.addCapability(ArsEngCore.makeId("generic_inv_wrapper"), genericInvProvider);
        event.addListener(genericInvProvider::invalidate);
    }
}
