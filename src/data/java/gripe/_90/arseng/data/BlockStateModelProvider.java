package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCore;

class BlockStateModelProvider extends BlockStateProvider {
    BlockStateModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, ArsEngCore.MODID, existing);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ArsEngBlocks.SOURCE_ACCEPTOR.block(), cubeAll(ArsEngBlocks.SOURCE_ACCEPTOR.block()));

        var sourceCell = "block/source_drive_cell";
        models().getBuilder(sourceCell)
                .parent(new ModelFile.UncheckedModelFile(AppEng.makeId("block/drive/drive_cell")))
                .texture("cell", ArsEngCore.makeId(sourceCell));
    }
}
