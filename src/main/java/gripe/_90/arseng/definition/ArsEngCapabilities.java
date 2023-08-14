package gripe._90.arseng.definition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class ArsEngCapabilities {
    public static final Capability<ISourceTile> SOURCE_TILE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ISourceTile.class);
    }

    public static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof ISourceTile sourceTile) {
            var provider = new ICapabilityProvider() {
                private final LazyOptional<ISourceTile> sourceHandler = LazyOptional.of(() -> sourceTile);

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(
                        @NotNull Capability<T> cap, @Nullable Direction side) {
                    return SOURCE_TILE.orEmpty(cap, sourceHandler);
                }

                private void invalidate() {
                    sourceHandler.invalidate();
                }
            };

            event.addCapability(ArsEngCore.makeId("source_tile"), provider);
            event.addListener(provider::invalidate);
        }
    }
}
