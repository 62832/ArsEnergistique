package gripe._90.arseng.ae2;

import java.util.List;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.capability.ArsEngCapabilities;

public class SourceP2PTunnelPart extends CapabilityP2PTunnelPart<SourceP2PTunnelPart, ISourceTile> {

    private static final P2PModels MODELS = new P2PModels(ArsEnergistique.makeId("part/source_p2p_tunnel"));
    private static final NullHandler NULL_SOURCE_HANDLER = new NullHandler();

    public SourceP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem, ArsEngCapabilities.SOURCE_PROVIDER);
        inputHandler = new InputHandler();
        outputHandler = new OutputHandler();
        emptyHandler = NULL_SOURCE_HANDLER;
    }

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    private class InputHandler implements ISourceTile {
        @Override
        public int getTransferRate() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canAcceptSource() {
            for (var part : getOutputs()) {
                try (var guard = part.getAdjacentCapability()) {
                    return guard.get().canAcceptSource();
                }
            }
            return false;
        }

        @Override
        public int getSource() {
            return 0;
        }

        @Override
        public int getMaxSource() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void setMaxSource(int max) {
        }

        @Override
        public int setSource(int source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int addSource(int source) {
            int total = 0;

            final int outputTunnels = getOutputs().size();
            if (outputTunnels == 0 || source == 0) {
                return 0;
            }

            final int amountPerOutput = source / outputTunnels;
            int overflow = amountPerOutput == 0 ? source : source % amountPerOutput;

            for (SourceP2PTunnelPart target : getOutputs()) {
                try (CapabilityGuard guard = target.getAdjacentCapability()) {
                    final ISourceTile output = guard.get();
                    final int toSend = amountPerOutput + overflow;
                    final int received = output.addSource(toSend);

                    overflow = toSend - received;
                    total += received;
                }
            }

            queueTunnelDrain(PowerUnits.RF, (double) total / SourceKeyType.TYPE.getAmountPerOperation());
            return total;
        }

        @Override
        public int removeSource(int source) {
            return 0;
        }
    }

    private class OutputHandler implements ISourceTile {

        @Override
        public int getTransferRate() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canAcceptSource() {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().canAcceptSource();
            }
        }

        @Override
        public int getSource() {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().getSource();
            }
        }

        @Override
        public int getMaxSource() {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().getMaxSource();
            }
        }

        @Override
        public void setMaxSource(int max) {
        }

        @Override
        public int setSource(int source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int addSource(int source) {
            return 0;
        }

        @Override
        public int removeSource(int source) {
            try (CapabilityGuard input = getInputCapability()) {
                int result = input.get().removeSource(0);
                queueTunnelDrain(PowerUnits.RF, (double) result / SourceKeyType.TYPE.getAmountPerOperation());
                return result;
            }
        }
    }

    private static class NullHandler implements ISourceTile {

        @Override
        public int getTransferRate() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canAcceptSource() {
            return false;
        }

        @Override
        public int getSource() {
            return 0;
        }

        @Override
        public int getMaxSource() {
            return 0;
        }

        @Override
        public void setMaxSource(int max) {
        }

        @Override
        public int setSource(int source) {
            return 0;
        }

        @Override
        public int addSource(int source) {
            return 0;
        }

        @Override
        public int removeSource(int source) {
            return 0;
        }
    }
}
