package gripe._90.arseng.part;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnit;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.util.AECableType;
import appeng.blockentity.powersink.IExternalPowerSink;
import appeng.items.parts.PartModels;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.me.misc.SourceEnergyAdaptor;

public class SourceConverterPart extends AEBasePart implements IExternalPowerSink {
    @PartModels
    private static final IPartModel MODEL = new PartModel(ArsEnergistique.makeId("part/source_acceptor"));

    private final SourceEnergyAdaptor adaptor = new SourceEnergyAdaptor(this, this);

    public SourceConverterPart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().setIdlePowerUsage(0);
    }

    public static void registerCapability(RegisterPartCapabilitiesEvent event) {
        event.register(CapabilityRegistry.SOURCE_CAPABILITY, (part, ctx) -> part.adaptor, SourceConverterPart.class);
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
    public final double getExternalPowerDemand(PowerUnit externalUnit, double maxPowerRequired) {
        var demand = getFunnelPowerDemand(externalUnit.convertTo(PowerUnit.AE, maxPowerRequired));
        return PowerUnit.AE.convertTo(externalUnit, Math.max(0.0, demand));
    }

    protected double getFunnelPowerDemand(double maxRequired) {
        var grid = getMainNode().getGrid();
        return grid != null ? grid.getEnergyService().getEnergyDemand(maxRequired) : 0;
    }

    @Override
    public final double injectExternalPower(PowerUnit input, double amt, Actionable mode) {
        return PowerUnit.AE.convertTo(input, funnelPowerIntoStorage(input.convertTo(PowerUnit.AE, amt), mode));
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
