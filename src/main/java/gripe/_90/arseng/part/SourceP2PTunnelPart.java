package gripe._90.arseng.part;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import appeng.api.config.PowerUnit;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngConfig;
import gripe._90.arseng.me.key.SourceKeyType;

public class SourceP2PTunnelPart extends CapabilityP2PTunnelPart<SourceP2PTunnelPart, ISourceCap> {
    private static final P2PModels MODELS = new P2PModels(ArsEnergistique.makeId("part/source_p2p_tunnel"));

    private static final EmptyHandler EMPTY_HANDLER = new EmptyHandler();

    public SourceP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem, CapabilityRegistry.SOURCE_CAPABILITY);
        inputHandler = new InputHandler();
        outputHandler = new OutputHandler();
        emptyHandler = EMPTY_HANDLER;
    }

    @SuppressWarnings("unused")
    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(isPowered(), isActive());
    }

    private OutputHandler getOutputHandler() {
        return (OutputHandler) outputHandler;
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        data.putInt("source", getOutputHandler().bufferSource);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        getOutputHandler().bufferSource = data.getInt("source");
    }

    private class InputHandler implements ISourceCap {
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
            for (var output : getOutputs()) {
                if (output.getOutputHandler().canAcceptLocalSource(source)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean canProvideSource(int source) {
            return false;
        }

        @Override
        public int getSource() {
            return getOutputStream()
                    .map(part -> part.getOutputHandler().getLocalSource())
                    .reduce(0, Integer::sum);
        }

        @Override
        public int getSourceCapacity() {
            return getOutputStream()
                    .map(part -> part.getOutputHandler().getLocalMaxSource())
                    .reduce(0, Integer::sum);
        }

        @Override
        public void setMaxSource(int max) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSource(int source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int receiveSource(int source, boolean simulate) {
            var outputs = getOutputStream()
                    .filter(part -> part.getOutputHandler().canAcceptLocalSource(source))
                    .toList();

            if (outputs.isEmpty()) {
                return 0;
            }

            if (!simulate) {
                queueTunnelDrain(PowerUnit.AE, source / 100D);
            }

            var forEach = source / outputs.size();
            var spill = new AtomicInteger(source % outputs.size());
            var total = new AtomicInteger(0);

            outputs.forEach(output -> total.addAndGet(output.getOutputHandler()
                    .addSourceRespectingBuffer(forEach + (spill.getAndDecrement() > 0 ? 1 : 0), simulate)));

            return total.get();
        }

        @Override
        public int extractSource(int source, boolean simulate) {
            return 0;
        }
    }

    private class OutputHandler implements ISourceCap {
        private static final int MAX_BUFFER = ArsEngConfig.OUTPUT_P2P_BUFFER.get();

        private int bufferSource = 0;

        private boolean canAcceptLocalSource(int source) {
            return getLocalSource() + source < getLocalMaxSource();
        }

        private int addSourceRespectingBuffer(int amount, boolean simulate) {
            int source = 0;

            try (var guard = getAdjacentCapability()) {
                var tile = guard.get();

                if (tile != null && !(tile instanceof EmptyHandler)) {
                    source += tile.receiveSource(amount, simulate);
                    amount = 0;
                }
            }

            // add to buffer only if no machine to add to
            bufferSource += amount;

            if (bufferSource > MAX_BUFFER) {
                bufferSource = MAX_BUFFER;
            }

            source += bufferSource;

            return source;
        }

        private int getLocalSource() {
            try (var guard = getAdjacentCapability()) {
                return bufferSource + guard.get().getSource();
            }
        }

        private int getLocalMaxSource() {
            try (var guard = getAdjacentCapability()) {
                return MAX_BUFFER + guard.get().getSourceCapacity();
            }
        }

        @Override
        public int getMaxExtract() {
            try (var input = getInputCapability()) {
                var tile = input.get();
                return tile != emptyHandler ? tile.getMaxExtract() : MAX_BUFFER;
            }
        }

        @Override
        public int getMaxReceive() {
            return 0;
        }

        @Override
        public boolean canProvideSource(int source) {
            return extractSource(source, true) > 0;
        }

        @Override
        public boolean canAcceptSource(int source) {
            return false;
        }

        @Override
        public int getSource() {
            try (var input = getInputCapability()) {
                return input.get().getSource() + bufferSource;
            }
        }

        @Override
        public int getSourceCapacity() {
            try (var input = getInputCapability()) {
                return input.get().getSourceCapacity() + MAX_BUFFER;
            }
        }

        @Override
        public void setMaxSource(int max) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSource(int source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int receiveSource(int source, boolean simulate) {
            return 0;
        }

        @Override
        public int extractSource(int source, boolean simulate) {
            // use buffer first
            if (bufferSource >= source) {
                bufferSource -= source;
                return 0;
            } else {
                bufferSource = 0;
            }

            try (var input = getInputCapability()) {
                var result = input.get().extractSource(source, simulate);

                if (!simulate) {
                    queueTunnelDrain(PowerUnit.AE, (double) result / SourceKeyType.TYPE.getAmountPerOperation());
                }

                return result;
            }
        }
    }

    private static class EmptyHandler implements ISourceCap {
        @Override
        public boolean canAcceptSource(int source) {
            return false;
        }

        @Override
        public boolean canProvideSource(int source) {
            return false;
        }

        @Override
        public int getMaxExtract() {
            return 0;
        }

        @Override
        public int getMaxReceive() {
            return 0;
        }

        @Override
        public int getSource() {
            return 0;
        }

        @Override
        public int getSourceCapacity() {
            return 0;
        }

        @Override
        public void setSource(int source) {}

        @Override
        public void setMaxSource(int max) {}

        @Override
        public int receiveSource(int source, boolean simulate) {
            return 0;
        }

        @Override
        public int extractSource(int source, boolean simulate) {
            return 0;
        }
    }
}
