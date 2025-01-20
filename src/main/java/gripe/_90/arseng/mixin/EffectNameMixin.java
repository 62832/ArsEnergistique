package gripe._90.arseng.mixin;

import appeng.api.ids.AEComponents;
import appeng.api.parts.IPart;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.util.SettingsFrom;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectName.class)
public class EffectNameMixin {
    @WrapOperation(method = "onResolveBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity onResolveBlock(Level instance, BlockPos pos, Operation<BlockEntity> original, @Cancellable CallbackInfo ci, @Local Component name, @Local(argsOnly = true) LivingEntity shooter, @Local(argsOnly = true) BlockHitResult bhr) {
        BlockEntity be = original.call(instance, pos);
        Player player = shooter instanceof Player playerEntity ? playerEntity : null;
        DataComponentMap map = DataComponentMap.builder().set(AEComponents.EXPORTED_CUSTOM_NAME, name).build();

        if (be instanceof CableBusBlockEntity cableBusBlock) {
            IPart part = cableBusBlock.getPart(bhr.getDirection().getOpposite());
            if (part != null) {
                part.importSettings(SettingsFrom.MEMORY_CARD, map, player);
            }
            ci.cancel();
        } else if (be instanceof AEBaseBlockEntity baseBlockEntity) {
            baseBlockEntity.importSettings(SettingsFrom.MEMORY_CARD, map, player);
            ci.cancel();
        }
        return be;
    }
}
