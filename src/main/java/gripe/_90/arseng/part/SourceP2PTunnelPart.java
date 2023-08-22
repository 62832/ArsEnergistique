package gripe._90.arseng.part;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import appeng.api.config.PowerUnits;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;

import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.definition.ArsEngConfig;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.key.SourceKeyType;

public class SourceP2PTunnelPart extends CapabilityP2PTunnelPart<SourceP2PTunnelPart, IAdvancedSourceTile> {
    private static final P2PModels MODELS = new P2PModels(ArsEngCore.makeId("part/source_p2p_tunnel"));

    private static final EmptyHandler EMPTY_HANDLER = new EmptyHandler();

    public SourceP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem, ArsEngCapabilities.SOURCE_TILE);
        inputHandler = new InputHandler();
        outputHandler = new OutputHandler();
        emptyHandler = EMPTY_HANDLER;
    }

    public static void initAttunement(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> P2PTunnelAttunement.registerAttunementTag(ArsEngItems.SOURCE_P2P_TUNNEL));
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
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putInt("source", getOutputHandler().bufferSource);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        getOutputHandler().bufferSource = data.getInt("source");
    }

    private class InputHandler implements IAdvancedSourceTile {
        @Override
        public int getTransferRate() {
            return getMaxSource();
        }

        @Override
        public boolean canAcceptSource() {
            for (SourceP2PTunnelPart part : getOutputs()) {
                if (part.getOutputHandler().canAcceptLocalSource()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int getSource() {
            return getOutputStream()
                    .map(part -> part.getOutputHandler().getLocalSource())
                    .reduce(0, Integer::sum);
        }

        @Override
        public int getMaxSource() {
            return getOutputStream()
                    .map(part -> part.getOutputHandler().getLocalMaxSource())
                    .reduce(0, Integer::sum);
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
            var outputs = getOutputStream()
                    .filter(part -> part.getOutputHandler().canAcceptLocalSource())
                    .toList();

            if (outputs.isEmpty()) {
                return 0;
            }

            queueTunnelDrain(PowerUnits.AE, source / 100D);
            var forEach = source / outputs.size();
            var spill = new AtomicInteger(source % outputs.size());
            var total = new AtomicInteger(0);

            outputs.forEach(output -> total.addAndGet(output.getOutputHandler()
                    .addSourceRespectingBuffer(forEach + (spill.getAndDecrement() > 0 ? 1 : 0))));

            return total.get();
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

    private class OutputHandler implements IAdvancedSourceTile {
        private static final int MAX_BUFFER = ArsEngConfig.OUTPUT_P2P_BUFFER.get();

        private int bufferSource = 0;

        private boolean canAcceptLocalSource() {
            return getLocalSource() < getLocalMaxSource();
        }

        private int addSourceRespectingBuffer(int amount) {
            int source = 0;

            try (var guard = getAdjacentCapability()) {
                var tile = guard.get();

                if (tile != null && !(tile instanceof EmptyHandler)) {
                    source += tile.addSource(amount);
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
                return MAX_BUFFER + guard.get().getMaxSource();
            }
        }

        @Override
        public int getTransferRate() {
            try (var input = getInputCapability()) {
                var tile = input.get();
                return tile != emptyHandler ? tile.getTransferRate() : MAX_BUFFER;
            }
        }

        @Override
        public boolean canAcceptSource() {
            return false;
        }

        @Override
        public int getSource() {
            try (var input = getInputCapability()) {
                return input.get().getSource() + bufferSource;
            }
        }

        @Override
        public int getMaxSource() {
            try (var input = getInputCapability()) {
                return input.get().getMaxSource() + MAX_BUFFER;
            }
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
            return 0;
        }

        @Override
        public int removeSource(int source) {
            // use buffer first
            if (bufferSource >= source) {
                bufferSource -= source;
                return 0;
            } else {
                bufferSource = 0;
            }

            try (var input = getInputCapability()) {
                var result = input.get().removeSource(source);
                queueTunnelDrain(PowerUnits.AE, (double) result / SourceKeyType.TYPE.getAmountPerOperation());
                return result;
            }
        }

        @Override
        public boolean relayCanTakePower() {
            return true;
        }

        @Override
        public boolean sourcelinksCanProvidePower() {
            return false;
        }
    }

    private static class EmptyHandler implements IAdvancedSourceTile {
        @Override
        public int getTransferRate() {
            return 0;
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
        public void setMaxSource(int max) {}

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

        @Override
        public boolean relayCanTakePower() {
            return false;
        }

        @Override
        public boolean sourcelinksCanProvidePower() {
            return false;
        }
    }
}
