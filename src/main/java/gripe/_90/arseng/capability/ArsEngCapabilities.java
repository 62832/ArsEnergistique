package gripe._90.arseng.capability;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import appeng.capabilities.Capabilities;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.part.MESourceStorage;

@SuppressWarnings("UnstableApiUsage")
public class ArsEngCapabilities {

    public static final Capability<ISourceTile> SOURCE_PROVIDER = CapabilityManager.get(
            new CapabilityToken<>() {
            });

    public static void init(AttachCapabilitiesEvent<BlockEntity> event) {
        var blockEntity = event.getObject();

        event.addCapability(ArsEnergistique.makeId("generic_inv_wrapper"), new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == SOURCE_PROVIDER) {
                    return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(inventory -> new MESourceStorage(blockEntity.getBlockPos(),
                                    blockEntity.getBlockState(), inventory))
                            .cast();
                }
                return LazyOptional.empty();
            }
        });
    }
}
