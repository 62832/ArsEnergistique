package gripe._90.arseng.mixin;

import appeng.api.networking.security.IActionSource;
import appeng.blockentity.misc.InterfaceBlockEntity;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gripe._90.arseng.me.misc.DirectInterfaceMapInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InvUtil.class)
public class InvUtilMixin {
    @WrapOperation(method = "adjacentInventories", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getCapability(Lnet/neoforged/neoforge/capabilities/BlockCapability;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object directInterfaceAdapter(Level instance, BlockCapability blockCapability, BlockPos pos, BlockState state, BlockEntity blockEntity, Object o, Operation<Object> original) {
        if (blockCapability == Capabilities.ItemHandler.BLOCK) {
            if (blockEntity == null) {
                blockEntity = instance.getBlockEntity(pos);
            }

            if (blockEntity instanceof InterfaceBlockEntity interfaceBe) {
                return new DirectInterfaceMapInventory(interfaceBe.getInterfaceLogic(), IActionSource.empty());
            }
        }

        return original.call(instance, blockCapability, pos, state, blockEntity, o);
    }
}
