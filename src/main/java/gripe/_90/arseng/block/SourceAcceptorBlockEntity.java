package gripe._90.arseng.block;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.networking.*;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.blockentity.powersink.IExternalPowerSink;
import appeng.me.InWorldGridNode;
import appeng.me.energy.StoredEnergyAmount;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class SourceAcceptorBlockEntity extends BlockEntity implements IExternalPowerSink, IGridConnectedBlockEntity, ICapabilityProvider, ISourceTile {

    // the current power buffer.
    private final StoredEnergyAmount stored = new StoredEnergyAmount(0, 10000, x -> {
    });

    IGridNodeListener nodeListener;

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("internalCurrentPower", stored.getAmount());
    }

    Logger logger = LoggerContext.getContext().getLogger("SourceAcceptor");

    @Override
    public IGridNode getGridNode(Direction dir) {
        logger.info("get grid node: "+dir);
        var node = this.getMainNode().getNode();

        return node;

        /*
        // We use the node rather than getGridConnectableSides since the node is already using absolute sides
        if (node instanceof InWorldGridNode inWorldGridNode
                && inWorldGridNode.isExposedOnSide(dir)) {
            logger.info("returning node");
            return node;
        }

        logger.warning("returning null!!!");
        return null;
         */
    }

    @Override
    public IGridNode getActionableNode() {
        logger.info("get actionable node");
        return getMainNode().getNode();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        stored.setStored(tag.getDouble("internalCurrentPower"));
    }

    public SourceAcceptorBlockEntity(BlockPos pos, BlockState state) {
        super(ArsEngBlocks.SOURCE_ACCEPTOR_TYPE.get(), pos, state);
        nodeListener = new NodeListener();
        getMainNode().setExposedOnSides(Set.of(Direction.values()));

    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        getMainNode().create(level,getBlockPos());
    }

    int PowerToSource(double power) {
        return (int) (power / 4);
    }

    double SourceToPower(int source) {
        return (double) source * 4;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ArsEngCapabilities.SOURCE_TILE) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
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
        return Math.min(PowerToSource(stored.getAmount()) + 1,getMaxSource());
    }

    @Override
    public int getMaxSource() {
        return PowerToSource(getAEMaxPower());
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
        funnelPowerIntoStorage(SourceToPower(source), Actionable.MODULATE);
        return Math.min(source + getSource(), getMaxSource());
    }

    @Override
    public int removeSource(int source) {
        throw new UnsupportedOperationException();
    }

    private final IManagedGridNode mainNode = createMainNode()
            .setVisualRepresentation(Items.CACTUS)//TODO: make it show the right block
            .addService(IAEPowerStorage.class, this)
            .setInWorldNode(true)
            .setTagName("proxy");

    class NodeListener implements IGridNodeListener{
        @Override
        public void onSaveChanges(Object nodeOwner, IGridNode node) {
            saveChanges();
        }
    }


    protected IManagedGridNode createMainNode() {
        if(nodeListener == null){
            nodeListener = new NodeListener();
        }
        Objects.requireNonNull(nodeListener);
        IManagedGridNode node = GridHelper.createManagedNode(this, nodeListener);
        return node;
    }

    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void saveChanges() {
        if (this.level == null) {
            return;
        }
        this.setChanged();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }


    protected double getFunnelPowerDemand(double maxRequired) {
        var grid = getMainNode().getGrid();
        if (grid != null) {
            return grid.getEnergyService().getEnergyDemand(maxRequired);
        } else {
            return stored.getMaximum() - stored.getAmount();
        }
    }

    protected double funnelPowerIntoStorage(double power, Actionable mode) {
        var grid = getMainNode().getGrid();
        if (grid != null) {
            return grid.getEnergyService().injectPower(power, mode);
        } else {
            return this.injectAEPower(power, mode);
        }
    }

    @Override
    public final double getExternalPowerDemand(PowerUnits externalUnit, double maxPowerRequired) {
        return PowerUnits.AE.convertTo(externalUnit,
                Math.max(0.0, this.getFunnelPowerDemand(externalUnit.convertTo(PowerUnits.AE, maxPowerRequired))));
    }

    @Override
    public final double injectExternalPower(PowerUnits input, double amt, Actionable mode) {
        return PowerUnits.AE.convertTo(input, this.funnelPowerIntoStorage(input.convertTo(PowerUnits.AE, amt), mode));
    }

    @Override
    public double injectAEPower(double amt, Actionable mode) {
        return amt - stored.insert(amt, mode == Actionable.MODULATE);
    }

    @Override
    public double getAEMaxPower() {
        return stored.getMaximum();
    }

    @Override
    public double getAECurrentPower() {
        return stored.getAmount();
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return false;
    }

    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }


    @Override
    public final double extractAEPower(double amt, Actionable mode, PowerMultiplier multiplier) {
        return multiplier.divide(this.extractAEPower(multiplier.multiply(amt), mode));
    }

    protected double extractAEPower(double amt, Actionable mode) {
        return stored.extract(amt, mode == Actionable.MODULATE);
    }
}
