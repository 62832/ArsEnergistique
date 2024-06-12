package gripe._90.arseng.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;

@Mixin(value = ConfigInventory.class, remap = false)
public abstract class ConfigInventoryMixin extends GenericStackInv {
    @Unique
    private GenericStack arseng$toStock;

    public ConfigInventoryMixin(@Nullable Runnable listener, int size) {
        super(listener, size);
    }

    @Shadow
    @Nullable
    public abstract GenericStack getStack(int slot);

    @Inject(method = "setStack", at = @At("HEAD"))
    private void rememberStack(int slot, GenericStack stack, CallbackInfo ci) {
        arseng$toStock = stack;
    }

    @ModifyVariable(method = "setStack", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    private GenericStack handleEmptyContainer(GenericStack stack, int slot) {
        return getStack(slot) == null && arseng$toStock != null ? new GenericStack(arseng$toStock.what(), 1) : null;
    }
}
