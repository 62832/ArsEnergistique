package gripe._90.arseng.block.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.util.AECableType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.powersink.IExternalPowerSink;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.energy.SourceEnergyAdapter;

public class SourceAcceptorBlockEntity extends AEBaseBlockEntity
        implements IExternalPowerSink, IInWorldGridNodeHost, IGridConnectedBlockEntity {
    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, BlockEntityNodeListener.INSTANCE)
            .setVisualRepresentation(getItemFromBlockEntity())
            .addService(IAEPowerStorage.class, this)
            .setIdlePowerUsage(0)
            .setInWorldNode(true)
            .setTagName("proxy");
    private LazyOptional<IAdvancedSourceTile> sourceTileOptional;

    public SourceAcceptorBlockEntity(BlockPos pos, BlockState state) {
        super(ArsEngBlocks.SOURCE_ACCEPTOR_ENTITY, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        getMainNode().saveToNBT(tag);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        getMainNode().loadFromNBT(data);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        scheduleInit();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        getMainNode().destroy();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        getMainNode().destroy();
    }

    @Override
    public void onReady() {
        super.onReady();
        getMainNode().create(getLevel(), getBlockPos());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        sourceTileOptional = LazyOptional.of(() -> new SourceEnergyAdapter(this, this));
    }

    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        var node = mainNode.getNode();
        return node != null && node.isExposedOnSide(dir) ? node : null;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ArsEngCapabilities.SOURCE_TILE.orEmpty(cap, sourceTileOptional);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        sourceTileOptional.invalidate();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    protected double getFunnelPowerDemand(double maxRequired) {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getEnergyService().getEnergyDemand(maxRequired) : 0;
    }

    protected double funnelPowerIntoStorage(double power, Actionable mode) {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getEnergyService().injectPower(power, mode) : 0;
    }

    @Override
    public final double getExternalPowerDemand(PowerUnits externalUnit, double maxPowerRequired) {
        return PowerUnits.AE.convertTo(
                externalUnit,
                Math.max(0.0, this.getFunnelPowerDemand(externalUnit.convertTo(PowerUnits.AE, maxPowerRequired))));
    }

    @Override
    public final double injectExternalPower(PowerUnits input, double amt, Actionable mode) {
        return PowerUnits.AE.convertTo(input, this.funnelPowerIntoStorage(input.convertTo(PowerUnits.AE, amt), mode));
    }

    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public double injectAEPower(double amt, Actionable mode) {
        return 0;
    }

    @Override
    public final double extractAEPower(double amt, Actionable mode, PowerMultiplier multiplier) {
        return 0;
    }

    @Override
    public double getAEMaxPower() {
        return 0;
    }

    @Override
    public double getAECurrentPower() {
        return 0;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return false;
    }
}
