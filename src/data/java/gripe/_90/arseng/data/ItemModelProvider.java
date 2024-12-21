package gripe._90.arseng.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngItems;

class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    ItemModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, ArsEnergistique.MODID, existing);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(ArsEngItems.SOURCE_CELL_HOUSING);
        flatSingleLayer(ArsEngItems.CREATIVE_SOURCE_CELL);

        ArsEngItems.getCells()
                .forEach(cell -> flatSingleLayer(cell).texture("layer1", AppEng.makeId("item/storage_cell_led")));
        ArsEngItems.getPortables().forEach(this::portable);

        var p2pTunnelItem = AppEng.makeId("item/p2p_tunnel_base");
        var p2pTunnelPart = AppEng.makeId("part/p2p/p2p_tunnel_base");
        var sourceP2P = ArsEnergistique.makeId("part/p2p_tunnel_source");
        var spellP2P = ArsEnergistique.makeId("part/p2p_tunnel_spell");
        withExistingParent("item/source_p2p_tunnel", p2pTunnelItem).texture("type", sourceP2P);
        withExistingParent("part/source_p2p_tunnel", p2pTunnelPart).texture("type", sourceP2P);
        withExistingParent("item/spell_p2p_tunnel", p2pTunnelItem).texture("type", spellP2P);
        withExistingParent("part/spell_p2p_tunnel", p2pTunnelPart).texture("type", spellP2P);

        withExistingParent("part/source_acceptor", AppEng.makeId("part/energy_acceptor"))
                .texture("front", "block/source_acceptor");
        withExistingParent("item/cable_source_acceptor", AppEng.makeId("item/cable_energy_acceptor"))
                .texture("front", "block/source_acceptor");

        driveCell("1k_source_cell", 0);
        driveCell("4k_source_cell", 2);
        driveCell("16k_source_cell", 4);
        driveCell("64k_source_cell", 6);
        driveCell("256k_source_cell", 8);
        driveCell("creative_source_cell", 12);
    }

    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item) {
        var path = item.id().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", ArsEnergistique.makeId("item/" + path));
    }

    private void portable(ItemDefinition<?> portable) {
        var id = portable.id().getPath();
        var tierSuffix = id.substring(id.lastIndexOf('_'));

        singleTexture(
                        id,
                        mcLoc("item/generated"),
                        "layer0",
                        ArsEnergistique.makeId("item/portable_source_cell_housing"))
                .texture("layer1", AppEng.makeId("item/portable_cell_led"))
                .texture("layer2", AppEng.makeId("item/portable_cell_screen"))
                .texture("layer3", AppEng.makeId("item/portable_cell_side" + tierSuffix));
    }

    private void driveCell(String cell, int offset) {
        getBuilder("block/drive/cells/" + cell)
                .ao(false)
                .texture("cell", "block/source_drive_cell")
                .texture("particle", "block/source_drive_cell")
                .element()
                .to(6, 2, 2)
                .face(Direction.NORTH)
                .uvs(0, offset, 6, offset + 2)
                .end()
                .face(Direction.UP)
                .uvs(6, offset, 0, offset + 2)
                .end()
                .face(Direction.DOWN)
                .uvs(6, offset, 0, offset + 2)
                .end()
                .faces((dir, builder) ->
                        builder.texture("#cell").cullface(Direction.NORTH).end())
                .end();
    }
}
