package gripe._90.arseng.mixin;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = RelaySplitterTile.class, remap = false)
public abstract class RelaySplitterTileMixin extends AbstractSourceMachine {

    @Shadow
    ArrayList<BlockPos> toList = new ArrayList<>();
    @Shadow
    ArrayList<BlockPos> fromList = new ArrayList<>();

    @Shadow
    public void createParticles(BlockPos from, BlockPos to) {
        ParticleUtil.spawnFollowProjectile(level, from, to);
    }

    public RelaySplitterTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }


    @Inject(method = "processFromList", at = @At("HEAD"), cancellable = true)
    void addCapToProcessFrom(CallbackInfo ci){
        ci.cancel();
        if (fromList.isEmpty())
            return;

        ArrayList<BlockPos> stale = new ArrayList<>();
        int ratePer = getTransferRate() / fromList.size();
        for (BlockPos fromPos : fromList) {
            if (!level.isLoaded(fromPos))
                continue;

            BlockEntity be = level.getBlockEntity(fromPos);
            if(be == null){
                stale.add(fromPos);
                continue;
            }

            LazyOptional<IAdvancedSourceTile> cap = be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(),fromPos));
            if(cap.isPresent()) {
                if (transferSource(cap.resolve().get(), this, ratePer) > 0) {
                    createParticles(fromPos, worldPosition);
                }
            }else{
                stale.add(fromPos);
                continue;
            }
        }
        for (BlockPos s : stale) {
            fromList.remove(s);
            updateBlock();
        }
    }

    @Inject(method = "processToList", at = @At("HEAD"), cancellable = true)
    void addCapToProcessTo(CallbackInfo ci){
        ci.cancel();
        if (toList.isEmpty())
            return;

        ArrayList<BlockPos> stale = new ArrayList<>();
        int ratePer = getTransferRate() / toList.size();
        for (BlockPos toPos : toList) {
            if (!level.isLoaded(toPos))
                continue;

            BlockEntity be = level.getBlockEntity(toPos);
            if(be == null){
                stale.add(toPos);
                continue;
            }

            LazyOptional<IAdvancedSourceTile> cap = be.getCapability(ArsEngCapabilities.SOURCE_TILE, IAdvancedSourceTile.getDirTo(getBlockPos(),toPos));
            if(cap.isPresent()) {
                if (transferSource(this, cap.resolve().get(), ratePer) > 0) {
                    createParticles(worldPosition, toPos);
                }
            }else{
                stale.add(toPos);
                continue;
            }
        }
        for (BlockPos s : stale) {
            fromList.remove(s);
            updateBlock();
        }
    }



}
