package gripe._90.arseng.me.misc;

import com.google.common.primitives.Ints;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnit;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.powersink.IExternalPowerSink;

import gripe._90.arseng.definition.ArsEngConfig;

public record SourceEnergyAdaptor(IExternalPowerSink sink, IActionHost host) implements ISourceCap {
    private static final double AE_PER_SOURCE = ArsEngConfig.AE_PER_SOURCE.get();

    @Override
    public int getMaxReceive() {
        return getSourceCapacity();
    }

    @Override
    public int getMaxExtract() {
        return 0;
    }

    @Override
    public boolean canAcceptSource(int source) {
        return getSource() + source < getSourceCapacity();
    }

    @Override
    public boolean canProvideSource(int source) {
        return false;
    }

    @Override
    public int getSource() {
        return Ints.saturatedCast(getSourceCapacity()
                - Math.round(sink.getExternalPowerDemand(PowerUnit.AE, getSourceCapacity()) / AE_PER_SOURCE));
    }

    @Override
    public int getSourceCapacity() {
        var grid = host.getActionableNode();
        return grid != null ? (int) (grid.getGrid().getEnergyService().getMaxStoredPower() / AE_PER_SOURCE) : 0;
    }

    @Override
    public int receiveSource(int source, boolean simulate) {
        sink.injectExternalPower(PowerUnit.AE, source * AE_PER_SOURCE, Actionable.ofSimulate(simulate));
        return Math.min(source + getSource(), getSourceCapacity());
    }

    @Override
    public int extractSource(int source, boolean simulate) {
        return 0;
    }

    @Override
    public void setMaxSource(int max) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSource(int source) {
        throw new UnsupportedOperationException();
    }
}
