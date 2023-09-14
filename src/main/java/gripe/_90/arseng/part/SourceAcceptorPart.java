package gripe._90.arseng.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.blockentity.powersink.IExternalPowerSink;
import appeng.items.parts.PartModels;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.me.energy.SourceEnergyAdapter;

public class SourceAcceptorPart extends AEBasePart implements IExternalPowerSink {
    @PartModels
    private static final IPartModel MODEL = new PartModel(ArsEnergistique.makeId("part/source_acceptor"));

    private final SourceEnergyAdapter adapter = new SourceEnergyAdapter(this, this);

    public SourceAcceptorPart(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode().setIdlePowerUsage(0);
    }

    @Override
    public IPartModel getStaticModels() {
        return MODEL;
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 2;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 12, 12, 12, 14);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        return ArsEngCapabilities.SOURCE_TILE.orEmpty(capability, LazyOptional.of(() -> adapter));
    }

    @Override
    public final double getExternalPowerDemand(PowerUnits externalUnit, double maxPowerRequired) {
        var demand = getFunnelPowerDemand(externalUnit.convertTo(PowerUnits.AE, maxPowerRequired));
        return PowerUnits.AE.convertTo(externalUnit, Math.max(0.0, demand));
    }

    protected double getFunnelPowerDemand(double maxRequired) {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getEnergyService().getEnergyDemand(maxRequired) : 0;
    }

    @Override
    public final double injectExternalPower(PowerUnits input, double amt, Actionable mode) {
        return PowerUnits.AE.convertTo(input, funnelPowerIntoStorage(input.convertTo(PowerUnits.AE, amt), mode));
    }

    protected double funnelPowerIntoStorage(double power, Actionable mode) {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getEnergyService().injectPower(power, mode) : power;
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
