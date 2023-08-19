package gripe._90.arseng.part;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import appeng.api.config.PowerUnits;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;

import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.key.SourceKeyType;

public class SourceP2PTunnelPart extends CapabilityP2PTunnelPart<SourceP2PTunnelPart, ISourceTile> {
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

    private class InputHandler implements ISourceTile {
        @Override
        public int getTransferRate() {
            return getMaxSource();
        }

        @Override
        public boolean canAcceptSource() {
            for (SourceP2PTunnelPart part : getOutputs()) {
                try (var guard = part.getAdjacentCapability()) {
                    if (guard.get().canAcceptSource()) {
                        return true;
                    }
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
            throw new UnsupportedOperationException();
        }

        @Override
        public int setSource(int source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int addSource(int source) {
            var outputs = getOutputStream()
                    .filter(part -> {
                        try (var guard = part.getAdjacentCapability()) {
                            return guard.get().canAcceptSource();
                        }
                    })
                    .toList();

            if (outputs.isEmpty()) {
                return 0;
            }

            queueTunnelDrain(PowerUnits.AE, source / 100D);
            var forEach = source / outputs.size();
            var spill = new AtomicInteger(source % outputs.size());

            return outputs.stream()
                    .map(output -> {
                        try (var guard = output.getAdjacentCapability()) {
                            return guard.get().addSource(forEach + (spill.getAndDecrement() > 0 ? 1 : 0));
                        }
                    })
                    .reduce(0, Integer::sum);
        }

        @Override
        public int removeSource(int source) {
            return 0;
        }
    }

    private class OutputHandler implements ISourceTile {
        @Override
        public int getTransferRate() {
            try (var input = getInputCapability()) {
                return input.get().getTransferRate();
            }
        }

        @Override
        public boolean canAcceptSource() {
            try (var input = getInputCapability()) {
                return input.get().canAcceptSource();
            }
        }

        @Override
        public int getSource() {
            try (var input = getInputCapability()) {
                return input.get().getSource();
            }
        }

        @Override
        public int getMaxSource() {
            try (var input = getInputCapability()) {
                return input.get().getMaxSource();
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
            try (var input = getInputCapability()) {
                var result = input.get().removeSource(source);
                queueTunnelDrain(PowerUnits.AE, (double) result / SourceKeyType.TYPE.getAmountPerOperation());
                return result;
            }
        }
    }

    private static class EmptyHandler implements ISourceTile {
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
    }
}
