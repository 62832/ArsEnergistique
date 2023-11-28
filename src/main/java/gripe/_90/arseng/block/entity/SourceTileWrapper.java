package gripe._90.arseng.block.entity;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

public record SourceTileWrapper(ISourceTile tile, boolean takeFrom, boolean giveTo) implements IAdvancedSourceTile {
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
        return takeFrom;
    }

    @Override
    public boolean sourcelinksCanProvidePower() {
        return giveTo;
    }
}
