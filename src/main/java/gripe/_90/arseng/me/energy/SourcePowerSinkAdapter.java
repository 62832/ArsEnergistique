package gripe._90.arseng.me.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.powersink.IExternalPowerSink;
import gripe._90.arseng.definition.IAdvancedSourceTile;

public class SourcePowerSinkAdapter implements IAdvancedSourceTile {

    public static int PowerToSource(double power) {
        return (int) (power / 8);
    }

    public static double SourceToPower(int source) {
        return (double) source * 8;
    }
    private IExternalPowerSink sink;
    private IActionHost host;

    public SourcePowerSinkAdapter(IExternalPowerSink sink, IActionHost host){
        this.sink = sink;
        this.host = host;
    }

    @Override
    public String toString() {
        return super.toString();
    }

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
        int max = getMaxSource();
        // shows up as full a lot sooner than it should, so that things stop sending unneeded power
        //also, we use our on conversion so power units is AE
        int source =
                Math.min(max, max - PowerToSource(sink.getExternalPowerDemand(PowerUnits.AE, Math.max(0, max - SourceToPower(1000)))) + 1000);
        return source;
    }

    @Override
    public int getMaxSource() {
        var grid = host.getActionableNode();

        if (grid != null) {
            return PowerToSource(grid.getGrid().getEnergyService().getMaxStoredPower());
        }
        return 0;
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
        sink.injectExternalPower(PowerUnits.AE, SourceToPower(source), Actionable.MODULATE);
        return Math.min(source + getSource(), getMaxSource());
    }

    @Override
    public int removeSource(int source) {
        throw new UnsupportedOperationException();
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
