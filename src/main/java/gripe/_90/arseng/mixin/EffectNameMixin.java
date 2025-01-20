package gripe._90.arseng.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.ids.AEComponents;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.util.SettingsFrom;

@Mixin(EffectName.class)
public class EffectNameMixin {
    // spotless:off
    @WrapOperation(
            method = "onResolveBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    // spotless:on
    private BlockEntity onResolveBlock(
            Level instance,
            BlockPos pos,
            Operation<BlockEntity> original,
            @Cancellable CallbackInfo ci,
            @Local Component name,
            @Local(argsOnly = true) LivingEntity shooter,
            @Local(argsOnly = true) BlockHitResult result) {
        var be = original.call(instance, pos);
        var player = shooter instanceof Player playerEntity ? playerEntity : null;
        var components = DataComponentMap.builder()
                .set(AEComponents.EXPORTED_CUSTOM_NAME, name)
                .build();

        if (be instanceof CableBusBlockEntity cableBus) {
            var part = cableBus.getPart(result.getDirection().getOpposite());

            if (part != null) {
                part.importSettings(SettingsFrom.MEMORY_CARD, components, player);
            }

            ci.cancel();
        } else if (be instanceof AEBaseBlockEntity aeBE) {
            aeBE.importSettings(SettingsFrom.MEMORY_CARD, components, player);
            ci.cancel();
        }

        return be;
    }
}
