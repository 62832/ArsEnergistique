package gripe._90.arseng.part;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.blockentity.powersink.IExternalPowerSink;
import appeng.capabilities.Capabilities;
import appeng.parts.AEBasePart;
import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.energy.SourceEnergyAdapter;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class SourceAcceptorPart extends AEBasePart implements IExternalPowerSink {

    private SourceEnergyAdapter adapter;
    private LazyOptional<IAdvancedSourceTile> lazyAdapter;
    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 12, 12, 12, 14);
    }

    public SourceAcceptorPart(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode().setIdlePowerUsage(0);
        this.adapter = new SourceEnergyAdapter(this,this);
        lazyAdapter = LazyOptional.of(() -> adapter);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        if (capability == ArsEngCapabilities.SOURCE_TILE) {
            return ArsEngCapabilities.SOURCE_TILE.orEmpty(capability, lazyAdapter);
        }

        return super.getCapability(capability);
    }

    @Override
    public final double getExternalPowerDemand(PowerUnits externalUnit, double maxPowerRequired) {
        return PowerUnits.AE.convertTo(externalUnit,
                Math.max(0.0, this.getFunnelPowerDemand(externalUnit.convertTo(PowerUnits.AE, maxPowerRequired))));
    }

    protected double getFunnelPowerDemand(double maxRequired) {
        var grid = getMainNode().getGrid();
        if (grid != null) {
            return grid.getEnergyService().getEnergyDemand(maxRequired);
        } else {
            return 0;
        }
    }

    @Override
    public final double injectExternalPower(PowerUnits input, double amt, Actionable mode) {
        return PowerUnits.AE.convertTo(input, this.funnelPowerIntoStorage(input.convertTo(PowerUnits.AE, amt), mode));
    }

    protected double funnelPowerIntoStorage(double power, Actionable mode) {
        var grid = getMainNode().getGrid();
        if (grid != null) {
            return grid.getEnergyService().injectPower(power, mode);
        } else {
            return power;
        }
    }

    @Override
    public final double injectAEPower(double amt, Actionable mode) {
        return amt;
    }

    @Override
    public final double getAEMaxPower() {
        return 0;
    }

    @Override
    public final double getAECurrentPower() {
        return 0;
    }

    @Override
    public final boolean isAEPublicPowerStorage() {
        return false;
    }

    @Override
    public final AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public final double extractAEPower(double amt, Actionable mode, PowerMultiplier multiplier) {
        return 0;
    }
}
