package gripe._90.arseng.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Inject(method = "setStack", at = @At("HEAD"))
    private void rememberStack(int slot, GenericStack stack, CallbackInfo ci) {
        arseng$toStock = stack;
    }

    // spotless:off
    @Redirect(
            method = "setStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/helpers/externalstorage/GenericStackInv;setStack(ILappeng/api/stacks/GenericStack;)V"))
    // spotless:on
    private void setForEmpty(GenericStackInv instance, int slot, GenericStack stack) {
        if (arseng$toStock != null && arseng$toStock.amount() <= 0) {
            var currentStack = instance.getStack(slot);
            stack = currentStack == null ? new GenericStack(arseng$toStock.what(), 1) : null;
        }

        super.setStack(slot, stack);
    }
}
