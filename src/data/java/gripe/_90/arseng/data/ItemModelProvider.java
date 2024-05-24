package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
                .texture("front", "block/source_acceptor")
        withExistingParent("item/cable_source_acceptor", AppEng.makeId("item/cable_energy_acceptor"))
                .texture("front", "block/source_acceptor");
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
                        "layer0", ArsEnergistique.makeId("item/portable_source_cell_housing"))
                .texture("layer1", AppEng.makeId("item/portable_cell_led"))
                .texture("layer2", ArsEnergistique.makeId("item/portable_cell_screen"))
                .texture("layer3", ArsEnergistique.makeId("item/portable_source_cell" + tierSuffix));
    }
}
