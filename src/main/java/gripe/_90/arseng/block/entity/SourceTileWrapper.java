package gripe._90.arseng.block.entity;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

public class SourceTileWrapper implements IAdvancedSourceTile {
    private final ISourceTile tile;
    private final boolean relayTake;
    private final boolean sourcelinkGive;

    public SourceTileWrapper(ISourceTile tile, boolean relayCanTake, boolean sourcelinkCanGive) {
        this.tile = tile;
        relayTake = relayCanTake;
        sourcelinkGive = sourcelinkCanGive;
    }

    @Override
    public int getTransferRate() {
        return tile.getTransferRate();
    }

    @Override
    public boolean canAcceptSource() {
        return tile.canAcceptSource();
    }

    @Override
    public int getSource() {
        return tile.getSource();
    }

    @Override
    public int getMaxSource() {
        return tile.getMaxSource();
    }

    @Override
    public void setMaxSource(int max) {
        tile.setMaxSource(max);
    }

    @Override
    public int setSource(int source) {
        return tile.setSource(source);
    }

    @Override
    public int addSource(int source) {
        return tile.addSource(source);
    }

    @Override
    public int removeSource(int source) {
        return tile.removeSource(source);
    }

    @Override
    public boolean relayCanTakePower() {
        return relayTake;
    }

    @Override
    public boolean sourcelinksCanProvidePower() {
        return sourcelinkGive;
    }
}
