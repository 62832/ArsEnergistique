package gripe._90.arseng.definition;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;

public interface IAdvancedSourceTile extends ISourceTile {
    boolean relayCanTakePower();

    boolean sourcelinksCanProvidePower();
}
