package gripe._90.arseng.block.entity;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnit;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.blockentity.powersink.IExternalPowerSink;

import gripe._90.arseng.definition.ArsEngBlockEntities;
import gripe._90.arseng.me.misc.SourceEnergyAdaptor;

public class SourceConverterBlockEntity extends AENetworkedBlockEntity implements IExternalPowerSink {
    private final SourceEnergyAdaptor adaptor = new SourceEnergyAdaptor(this, this);

    public SourceConverterBlockEntity(BlockPos pos, BlockState state) {
        super(ArsEngBlockEntities.SOURCE_CONVERTER.get(), pos, state);
        getMainNode().setIdlePowerUsage(0);
    }

    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST, ArsEngBlockEntities.SOURCE_CONVERTER.get(), (be, ctx) -> be);
        event.registerBlockEntity(
                CapabilityRegistry.SOURCE_CAPABILITY,
                ArsEngBlockEntities.SOURCE_CONVERTER.get(),
                (be, ctx) -> be.adaptor);
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
    public final double getExternalPowerDemand(PowerUnit externalUnit, double maxPowerRequired) {
        var demand = getFunnelPowerDemand(externalUnit.convertTo(PowerUnit.AE, maxPowerRequired));
        return PowerUnit.AE.convertTo(externalUnit, Math.max(0.0, demand));
    }

    @Override
    public final double injectExternalPower(PowerUnit input, double amt, Actionable mode) {
        return PowerUnit.AE.convertTo(input, funnelPowerIntoStorage(input.convertTo(PowerUnit.AE, amt), mode));
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
