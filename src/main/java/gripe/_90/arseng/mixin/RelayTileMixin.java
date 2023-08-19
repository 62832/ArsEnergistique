package gripe._90.arseng.mixin;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.part.SourceP2PTunnelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = RelayTile.class, remap = false)
public abstract class RelayTileMixin extends AbstractSourceMachine {

    @Shadow
    private BlockPos toPos;
    @Shadow
    private BlockPos fromPos;

    @Shadow
    public boolean disabled;

    @Shadow
    public int getMaxDistance() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean setTakeFrom(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    public boolean setSendTo(BlockPos pos) {
        throw new UnsupportedOperationException();
    }


    @Shadow public abstract int getMaxSource();

    public RelayTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Inject(method = "setSendTo", at = @At("HEAD"), cancellable = true)
    public void addCapSetSend(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (BlockUtil.distanceFrom(pos, this.worldPosition) <= getMaxDistance() && !pos.equals(getBlockPos())) {
            BlockEntity be = level.getBlockEntity(pos);

            LazyOptional<IAdvancedSourceTile> cap = be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(),pos));
            if(cap.isPresent()) {
                this.toPos = pos;
                updateBlock();
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onFinishedConnectionFirst", at = @At("HEAD"), cancellable = true)

    public void addCapFinishedConnectFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
        //set send to actually calls checks on if it's valid
        //so doing it here is redundant.
        //the inject is only needed at all because RelayTile checks twice, redundantly, for some ungodly reason
        if (this.setSendTo(storedPos.immutable())) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", DominionWand.getPosString(storedPos)));
            ParticleUtil.beam(storedPos, worldPosition, level);
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }

        ci.cancel();
    }

    @Inject(method = "onFinishedConnectionLast", at = @At("HEAD"), cancellable = true)

    public void addCapFinishedConnectLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
        //set take from actually calls checks on if it's valid
        //so doing it here is redundant.
        //the inject is only needed at all because RelayTile checks twice, redundantly, for some ungodly reason
        if (this.setTakeFrom(storedPos.immutable())) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.take", DominionWand.getPosString(storedPos)));
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
        }

        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void addCapToTick(CallbackInfo ci){
        ci.cancel();
        if (level.isClientSide || disabled) {
            return;
        }
        if (level.getGameTime() % 20 != 0)
            return;

        if (fromPos != null && level.isLoaded(fromPos)) {
            BlockEntity be = level.getBlockEntity(fromPos);
            if(be == null){
                fromPos = null;
                updateBlock();
                return;
            }
            else {
                LazyOptional<IAdvancedSourceTile> cap = be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(),fromPos));
                if (cap.isPresent()) {
                    IAdvancedSourceTile tile = cap.resolve().get();
                    if (transferSource(tile, this) > 0) {
                        updateBlock();
                        ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition);
                    }
                }
            }
        }

        if (toPos != null && level.isLoaded(toPos)) {
            BlockEntity be = level.getBlockEntity(toPos);
            if(be == null){
                toPos = null;
                updateBlock();
                return;
            }
            else {
                LazyOptional<IAdvancedSourceTile> cap = be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(),toPos));
                if (cap.isPresent() && transferSource(this, cap.resolve().get()) > 0) {
                    updateBlock();
                    ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos);
                }
            }
        }
    }
    
}
