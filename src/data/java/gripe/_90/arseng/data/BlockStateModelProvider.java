package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngBlocks;

class BlockStateModelProvider extends BlockStateProvider {
    BlockStateModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, ArsEnergistique.MODID, existing);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ArsEngBlocks.SOURCE_ACCEPTOR.block(), cubeAll(ArsEngBlocks.SOURCE_ACCEPTOR.block()));

        var sourceJarModel = models().getExistingFile(ArsEngBlocks.ME_SOURCE_JAR.id());
        horizontalBlock(ArsEngBlocks.ME_SOURCE_JAR.block(), sourceJarModel);
        simpleBlockItem(ArsEngBlocks.ME_SOURCE_JAR.block(), sourceJarModel);

        var sourceCell = "block/source_drive_cell";
        models().getBuilder(sourceCell)
                .parent(new ModelFile.UncheckedModelFile(AppEng.makeId("block/drive/drive_cell")))
                .texture("cell", ArsEnergistique.makeId(sourceCell));
    }
}
