package gripe._90.arseng.block.entity;

import java.util.Set;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.IActionSource;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.storage.StorageHelper;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.grid.AENetworkedBlockEntity;

import gripe._90.arseng.definition.ArsEngBlockEntities;
import gripe._90.arseng.me.key.SourceKey;

public class MESourceJarBlockEntity extends AENetworkedBlockEntity implements ISourceCap, ServerTickingBlockEntity {
    private final IActionSource actionSource = IActionSource.ofMachine(this);
    private int clientSideFill;

    public MESourceJarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ArsEngBlockEntities.ME_SOURCE_JAR.get(), pos, blockState);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST, ArsEngBlockEntities.ME_SOURCE_JAR.get(), (be, ctx) -> be);
        event.registerBlockEntity(
                CapabilityRegistry.SOURCE_CAPABILITY, ArsEngBlockEntities.ME_SOURCE_JAR.get(), (be, ctx) -> be);
    }

    @Override
    public int receiveSource(int source, boolean simulate) {
        var grid = getMainNode().getGrid();
        return grid != null && getMainNode().isActive()
                ? (int) StorageHelper.poweredInsert(
                        grid.getEnergyService(),
                        grid.getStorageService().getInventory(),
                        SourceKey.KEY,
                        source,
                        actionSource,
                        Actionable.ofSimulate(simulate))
                : 0;
    }

    @Override
    public int extractSource(int source, boolean simulate) {
        var grid = getMainNode().getGrid();
        return grid != null && getMainNode().isActive()
                ? (int) StorageHelper.poweredExtraction(
                        grid.getEnergyService(),
                        grid.getStorageService().getInventory(),
                        SourceKey.KEY,
                        source,
                        actionSource,
                        Actionable.ofSimulate(simulate))
                : 0;
    }

    @Override
    public boolean canAcceptSource(int source) {
        return receiveSource(source, true) > 0;
    }

    @Override
    public boolean canProvideSource(int source) {
        return extractSource(source, true) > 0;
    }

    @Override
    public int getMaxExtract() {
        return getMainNode().isActive() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public int getMaxReceive() {
        return getMainNode().isActive() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public int getSource() {
        var grid = getMainNode().getGrid();

        if (grid == null) {
            return clientSideFill;
        }

        return getMainNode().isActive()
                ? (int) grid.getStorageService()
                        .getInventory()
                        .extract(SourceKey.KEY, Integer.MAX_VALUE, Actionable.SIMULATE, actionSource)
                : 0;
    }

    @Override
    public int getSourceCapacity() {
        var grid = getMainNode().getGrid();

        if (grid == null) {
            return SourceKey.MAX_SOURCE;
        }

        return getMainNode().isActive()
                ? (int) grid.getStorageService()
                        .getInventory()
                        .insert(SourceKey.KEY, Integer.MAX_VALUE, Actionable.SIMULATE, actionSource)
                : 0;
    }

    @Override
    public void setSource(int source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxSource(int max) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeVarInt(clampedFill());
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        var oldFill = clientSideFill;
        clientSideFill = data.readVarInt();
        return changed || oldFill != clientSideFill;
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);
        data.putInt("fill", clampedFill());
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);
        clientSideFill = data.getInt("fill");
    }

    @Override
    public void serverTick() {
        // update client state
        var fill = clampedFill();

        if (clientSideFill != fill) {
            clientSideFill = fill;
            saveChanges();
            markForUpdate();
        }
    }

    public int clampedFill() {
        return Math.min(getSource(), SourceKey.MAX_SOURCE);
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return Set.of(Direction.UP, Direction.DOWN, orientation.getSide(RelativeSide.BACK));
    }
}
