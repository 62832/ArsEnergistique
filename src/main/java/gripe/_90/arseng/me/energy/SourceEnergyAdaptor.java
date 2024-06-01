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
        return (int) Math.min(max, max - (sink.getExternalPowerDemand(PowerUnits.AE, max) / AE_PER_SOURCE) + 1000);
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
