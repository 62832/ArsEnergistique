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
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.key.SourceKeyType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class SourceP2PTunnelPart extends CapabilityP2PTunnelPart<SourceP2PTunnelPart, IAdvancedSourceTile> {
    private static final P2PModels MODELS = new P2PModels(ArsEngCore.makeId("part/source_p2p_tunnel"));

    private static final EmptyHandler EMPTY_HANDLER = new EmptyHandler();

    //please keep this here thanks
    static Logger logger = LoggerContext.getContext().getLogger("Source Tunnel");

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

    private class InputHandler implements IAdvancedSourceTile {

        @Override
        public int getTransferRate() {
            return getMaxSource();
        }

        @Override
        public boolean canAcceptSource() {
            for (SourceP2PTunnelPart part : getOutputs()) {
                if(getOutputHandler(part).canAcceptLocalSource()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int getSource() {
            int source = 0;
            for (SourceP2PTunnelPart part : getOutputs()) {
                source += getOutputHandler(part).getLocalSource();
            }
            return source;
        }

        @Override
        public int getMaxSource() {
            int max = 0;
            for (SourceP2PTunnelPart part : getOutputs()) {
                max += getOutputHandler(part).getLocalMaxSource();
            }
            return max;
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
                        return getOutputHandler(part).canAcceptLocalSource();
                    })
                    .toList();

            if (outputs.isEmpty()) {
                return 0;
            }

            queueTunnelDrain(PowerUnits.AE, source / 100D);
            var forEach = source / outputs.size();
            var spill = new AtomicInteger(source % outputs.size());
            var total = new AtomicInteger(0);

            outputs.stream()
                    .forEach(output -> {
                        total.addAndGet(getOutputHandler(output).addSourceRespectingBuffer(forEach + (spill.getAndDecrement() > 0 ? 1 : 0)));
                    });

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

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        OutputHandler output = getOutputHandler(this);
        if(output != null) {
            data.putInt("source",output.bufferSource);
        }
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        OutputHandler output = getOutputHandler(this);
        if(output != null && data.contains("source")) {
            output.bufferSource = data.getInt("source");
        }
    }


    static OutputHandler getOutputHandler(SourceP2PTunnelPart output){
        if(!output.isOutput()){
            return null;
        }
        if(output.outputHandler instanceof OutputHandler handler){
            return handler;
        }
        logger.warn("couldn't cast output handler! ");
        return null;
    }

    private class OutputHandler implements IAdvancedSourceTile {
        public int bufferSource = 0;
        public int bufferMax = 1000;

        public boolean canAcceptLocalSource(){
            return getLocalSource() < getLocalMaxSource();
        }

        public int addSourceRespectingBuffer(int amount){
            int sourceVal = 0;

            try (var adj = getAdjacentCapability()) {
                IAdvancedSourceTile tile = adj.get();
                if(tile != null && !(tile instanceof EmptyHandler)) {
                    sourceVal += tile.addSource(amount);
                    amount = 0;
                }
            }

            //add to buffer only if no machine to add to
            bufferSource += amount;
            if(bufferSource > bufferMax){
                bufferSource = bufferMax;
            }
            sourceVal += bufferSource;

            return sourceVal;
        }

        public int getLocalSource(){
            int source = bufferSource;
            try (var adj = getAdjacentCapability()) {
                IAdvancedSourceTile tile = adj.get();
                source += tile.getSource();
            }
            return source;
        }

        public int getLocalMaxSource(){
            int max = bufferMax;
            try (var adj = getAdjacentCapability()) {
                IAdvancedSourceTile tile = adj.get();
                max += tile.getMaxSource();
            }
            return max;
        }

        @Override
        public int getTransferRate() {
            try (var input = getInputCapability()) {
                IAdvancedSourceTile tile = input.get();
                int rate = 0;
                if(tile != null && !(tile instanceof EmptyHandler)) {
                    rate = tile.getTransferRate();
                }
                else{
                    rate = 1000;
                }
                return rate;
            }
        }

        @Override
        public boolean canAcceptSource() {
            try (var input = getInputCapability()) {
                IAdvancedSourceTile tile = input.get();
                if(tile != null && !(tile instanceof EmptyHandler)) {
                    return tile.canAcceptSource();
                }
                else{
                    return false;
                }
            }
        }

        @Override
        public int getSource() {
            try (var input = getInputCapability()) {
                int source = input.get().getSource() + bufferSource;
                return source;
            }
        }

        @Override
        public int getMaxSource() {
            try (var input = getInputCapability()) {
                return input.get().getMaxSource() + bufferMax;
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
            //use buffer first
            if(bufferSource >= source){
                bufferSource -= source;
                return 0;
            }
            else{
                bufferSource = 0;
                source -= bufferSource;
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
