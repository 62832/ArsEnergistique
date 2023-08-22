package gripe._90.arseng.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem.ConvertBuddingGoal;

import net.minecraft.core.BlockPos;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.entity.CertusGolem;

@Mixin(value = ConvertBuddingGoal.class, remap = false)
public abstract class ConvertBuddingGoalMixin {
    @Shadow
    BlockPos targetCluster;

    @Shadow
    public AmethystGolem golem;

    @Shadow
    public Supplier<Boolean> canUse;

    @Inject(method = "convert", at = @At("HEAD"))
    private void convertCertus(CallbackInfo ci) {
        if (targetCluster != null
                && golem.level.getBlockState(targetCluster).getBlock() == AEBlocks.QUARTZ_BLOCK.block()) {
            golem.level.setBlock(
                    targetCluster, AEBlocks.FLAWED_BUDDING_QUARTZ.block().defaultBlockState(), 3);
            ParticleUtil.spawnTouchPacket(golem.level, targetCluster, ParticleUtil.defaultParticleColorWrapper());
        }
    }

    @Inject(
            method = "start",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lcom/hollingsworth/arsnouveau/common/entity/AmethystGolem;goalState:Lcom/hollingsworth/arsnouveau/common/entity/AmethystGolem$AmethystGolemGoalState;"))
    private void targetCertus(CallbackInfo ci) {
        for (var pos : ((CertusGolem) golem).arseng$getCertusBlocks()) {
            if (golem.level.getBlockState(pos).getBlock() == AEBlocks.QUARTZ_BLOCK.block()) {
                targetCluster = pos;
                break;
            }
        }
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void accountForCertus(CallbackInfoReturnable<Boolean> cir) {
        var blocksEmpty = golem.amethystBlocks.isEmpty()
                && ((CertusGolem) golem).arseng$getCertusBlocks().isEmpty();
        cir.setReturnValue(canUse.get() && golem.convertCooldown <= 0 && !blocksEmpty);
    }
}
