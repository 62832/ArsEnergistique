package gripe._90.arseng.me.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.powersink.IExternalPowerSink;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngConfig;

public record SourceEnergyAdaptor(IExternalPowerSink sink, IActionHost host) implements IAdvancedSourceTile {
    private static final double AE_PER_SOURCE = ArsEngConfig.AE_PER_SOURCE.get();

    @Override
    public int getTransferRate() {
        return getMaxSource();
    }

    @Override
    public boolean canAcceptSource() {
        return getSource() < getMaxSource();
    }

    @Override
    public int getSource() {
        var max = getMaxSource();
        // shows up as full a lot sooner than it should, so that things stop sending unneeded power
        // also, we use our own conversion so power units is AE
        var earlyCap = sink.getExternalPowerDemand(PowerUnits.AE, Math.max(0, max - 1000 * AE_PER_SOURCE));
        return (int) Math.min(max, max - (earlyCap / AE_PER_SOURCE) + 1000);
    }

    @Override
    public int getMaxSource() {
        var grid = host.getActionableNode();
        return grid != null ? (int) (grid.getGrid().getEnergyService().getMaxStoredPower() / AE_PER_SOURCE) : 0;
    }

    @Override
    public void setMaxSource(int max) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setSource(int source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addSource(int source) {
        sink.injectExternalPower(PowerUnits.AE, source * AE_PER_SOURCE, Actionable.MODULATE);
        return Math.min(source + getSource(), getMaxSource());
    }

    @Override
    public int removeSource(int source) {
        return 0;
    }

    @Override
    public boolean relayCanTakePower() {
        return false;
    }

    @Override
    public boolean sourcelinksCanProvidePower() {
        return true;
    }
}
