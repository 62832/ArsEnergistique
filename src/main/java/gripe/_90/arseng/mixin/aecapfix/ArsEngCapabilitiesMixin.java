package gripe._90.arseng.mixin.aecapfix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import appeng.capabilities.Capabilities;

import gripe._90.aecapfix.misc.DirectionalCapabilityCache;
import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.misc.GenericStackSourceStorage;

@Mixin(value = ArsEngCapabilities.class, remap = false)
public abstract class ArsEngCapabilitiesMixin {
    // spotless:off
    @Inject(
            method = "attach",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/event/AttachCapabilitiesEvent;addCapability(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraftforge/common/capabilities/ICapabilityProvider;)V",
                    ordinal = 1),
            cancellable = true)
    // spotless:on
    private static void replaceInvWrapper(AttachCapabilitiesEvent<BlockEntity> event, CallbackInfo ci) {
        ci.cancel();

        var genericInvWrapper = new ICapabilityProvider() {
            private final DirectionalCapabilityCache<ISourceTile> sourceHandlers = new DirectionalCapabilityCache<>();

            @SuppressWarnings("UnstableApiUsage")
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ArsEngCapabilities.SOURCE_TILE) {
                    var holder = event.getObject()
                            .getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(GenericStackSourceStorage::new);
                    return sourceHandlers.getOrCache(side, holder).cast();
                }

                return LazyOptional.empty();
            }

            private void invalidate() {
                sourceHandlers.invalidate();
            }
        };

        event.addCapability(ArsEnergistique.makeId("generic_inv_wrapper"), genericInvWrapper);
        event.addListener(genericInvWrapper::invalidate);
    }
}
