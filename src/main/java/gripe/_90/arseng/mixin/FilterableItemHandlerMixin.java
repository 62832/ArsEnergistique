package gripe._90.arseng.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import gripe._90.arseng.me.misc.DirectInterfaceMapInventory;

@Mixin(FilterableItemHandler.class)
public class FilterableItemHandlerMixin {
    @Shadow
    private IItemHandler handler;

    @Inject(method = "insertItemStacked", at = @At("HEAD"), cancellable = true)
    private void directInterfaceInsert(ItemStack stack, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (this.handler instanceof DirectInterfaceMapInventory map) {
            cir.setReturnValue(map.insertStack(stack, false));
        }
    }
}
