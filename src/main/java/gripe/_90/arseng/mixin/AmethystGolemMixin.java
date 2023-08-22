package gripe._90.arseng.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.entity.CertusGolem;

@Mixin(value = AmethystGolem.class, remap = false)
public abstract class AmethystGolemMixin extends PathfinderMob implements CertusGolem {
    @Unique
    private final List<BlockPos> arseng$certusBlocks = new ArrayList<>();

    protected AmethystGolemMixin(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Nullable
    @Shadow
    public abstract BlockPos getHome();

    @Override
    public List<BlockPos> arseng$getCertusBlocks() {
        return arseng$certusBlocks;
    }

    @Inject(method = "scanBlocks", at = @At("HEAD"))
    private void scanForCertusBlocks(CallbackInfo ci) {
        if (getHome() != null) {
            arseng$certusBlocks.clear();
            var home = getHome().immutable();

            for (var pos : BlockPos.betweenClosed(
                    home.below(3).south(5).east(5), home.above(10).north(5).west(5))) {
                if (level.getBlockState(pos).isAir()) continue;

                if (level.getBlockState(pos).getBlock() == AEBlocks.QUARTZ_BLOCK.block()) {
                    arseng$certusBlocks.add(pos.immutable());
                }
            }
        }
    }
}
