package gripe._90.arseng.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;

@Mixin(value = RelayTile.class, remap = false)
public abstract class RelayTileMixin extends AbstractSourceMachine {
    @Shadow
    private BlockPos toPos;

    @Shadow
    private BlockPos fromPos;

    @Shadow
    public boolean disabled;

    @Shadow
    public abstract int getMaxDistance();

    @Shadow
    public abstract boolean setTakeFrom(BlockPos pos);

    @Shadow
    public abstract boolean setSendTo(BlockPos pos);

    public RelayTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Inject(method = "setSendTo", at = @At("HEAD"), cancellable = true)
    private void addCapSetSend(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (BlockUtil.distanceFrom(pos, this.worldPosition) <= getMaxDistance() && !pos.equals(getBlockPos())) {
            var be = Objects.requireNonNull(level).getBlockEntity(pos);

            if (be != null) {
                var cap = be.getCapability(
                        ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(), pos));

                if (cap.isPresent()) {
                    this.toPos = pos;
                    updateBlock();
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "onFinishedConnectionFirst", at = @At("HEAD"), cancellable = true)
    public void addCapFinishedConnectFirst(
            BlockPos storedPos, LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
        // setSendTo actually calls checks on if it's valid so doing it here is redundant.
        // the injection is only needed at all because RelayTile checks twice, redundantly, for some ungodly reason
        if (this.setSendTo(storedPos.immutable())) {
            PortUtil.sendMessage(
                    playerEntity,
                    Component.translatable("ars_nouveau.connections.send", DominionWand.getPosString(storedPos)));
            ParticleUtil.beam(storedPos, worldPosition, level);
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }

        ci.cancel();
    }

    @Inject(method = "onFinishedConnectionLast", at = @At("HEAD"), cancellable = true)
    public void addCapFinishedConnectLast(
            BlockPos storedPos, LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
        // setTakeFrom actually calls checks on if it's valid so doing it here is redundant.
        // the injection is only needed at all because RelayTile checks twice, redundantly, for some ungodly reason
        if (this.setTakeFrom(storedPos.immutable())) {
            PortUtil.sendMessage(
                    playerEntity,
                    Component.translatable("ars_nouveau.connections.take", DominionWand.getPosString(storedPos)));
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }

        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void addCapToTick(CallbackInfo ci) {
        ci.cancel();
        Objects.requireNonNull(level);

        if (level.isClientSide || disabled) return;
        if (level.getGameTime() % 20 != 0) return;

        if (fromPos != null && level.isLoaded(fromPos)) {
            var be = level.getBlockEntity(fromPos);

            if (be == null) {
                fromPos = null;
                updateBlock();
                return;
            } else {
                var cap = be.getCapability(
                        ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(), fromPos));
                cap.ifPresent(tile -> {
                    if (transferSource(tile, this) > 0) {
                        updateBlock();
                        ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition);
                    }
                });
            }
        }

        if (toPos != null && level.isLoaded(toPos)) {
            var be = level.getBlockEntity(toPos);

            if (be == null) {
                toPos = null;
                updateBlock();
            } else {
                var cap = be.getCapability(
                        ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(), toPos));
                cap.ifPresent(tile -> {
                    if (transferSource(this, tile) > 0) {
                        updateBlock();
                        ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos);
                    }
                });
            }
        }
    }
}
