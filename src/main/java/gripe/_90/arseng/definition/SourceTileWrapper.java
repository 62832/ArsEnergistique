package gripe._90.arseng.definition;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

public class SourceTileWrapper implements IAdvancedSourceTile {
    ISourceTile baseTile;
    boolean relayTake;
    boolean sourcelinkGive;

    public SourceTileWrapper(ISourceTile tile){
        baseTile = tile;
        relayTake = true;
        sourcelinkGive = true;
    }

    public SourceTileWrapper(ISourceTile tile, boolean relayCanTake, boolean sourcelinkCanGive){
        baseTile = tile;
        relayTake = relayCanTake;
        sourcelinkGive = sourcelinkCanGive;
    }

    public SourceTileWrapper withRelayTakePower(boolean value){
        relayTake = value;
        return this;
    }

    public SourceTileWrapper withSourcelinkProvidePower(boolean value){
        sourcelinkGive = value;
        return this;
    }


    @Override
    public int getTransferRate() {
        return baseTile.getTransferRate();
    }

    @Override
    public boolean canAcceptSource() {
        return baseTile.canAcceptSource();
    }

    @Override
    public int getSource() {
        return baseTile.getSource();
    }

    @Override
    public int getMaxSource() {
        return baseTile.getMaxSource();
    }

    @Override
    public void setMaxSource(int max) {
        baseTile.setMaxSource(max);
    }

    @Override
    public int setSource(int source) {
        return baseTile.setSource(source);
    }

    @Override
    public int addSource(int source) {
        return baseTile.addSource(source);
    }

    @Override
    public int removeSource(int source) {
        return baseTile.removeSource(source);
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
