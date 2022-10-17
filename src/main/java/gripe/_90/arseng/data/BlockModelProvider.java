package gripe._90.arseng.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.arseng.ArsEnergistique;

public class BlockModelProvider extends net.minecraftforge.client.model.generators.BlockModelProvider {

    private static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public BlockModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, ArsEnergistique.MODID, efh);
        efh.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        withExistingParent("block/drive/cells/source_storage_cell", DRIVE_CELL).texture("cell",
                "block/drive/cells/source_storage_cell");
    }
}
