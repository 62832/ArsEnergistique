package gripe._90.arseng.mixin;

import appeng.api.implementations.blockentities.IColorableBlockEntity;
import appeng.api.util.AEColor;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectWololo;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectWololo.class)
public class EffectWololoMixin {
    @Inject(method = "onResolveBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private void colorParts(BlockHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, CallbackInfo ci, @Local(name = "blockEntity") BlockEntity blockEntity, @Local(name = "dye") DyeItem dye) {
        if (world instanceof ServerLevel level && blockEntity instanceof IColorableBlockEntity colorable) {
            colorable.recolourBlock(rayTraceResult.getDirection(), AEColor.fromDye(dye.getDyeColor()), ANFakePlayer.getOrFakePlayer(level, shooter));
            ci.cancel();
        }
    }
}
