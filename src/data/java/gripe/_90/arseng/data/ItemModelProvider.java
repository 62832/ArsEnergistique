package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngItems;

class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {
    private static final ResourceLocation P2P_TUNNEL_BASE_ITEM = AppEng.makeId("item/p2p_tunnel_base");
    private static final ResourceLocation P2P_TUNNEL_BASE_PART = AppEng.makeId("part/p2p/p2p_tunnel_base");
    private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");
    private static final ResourceLocation ENERGY_ACCEPTOR_PART = AppEng.makeId("part/energy_acceptor");
    private static final ResourceLocation ENERGY_ACCEPTOR_PART_ITEM = AppEng.makeId("item/cable_energy_acceptor");

    private static final ResourceLocation SOURCE_GEM_BLOCK =
            new ResourceLocation("ars_nouveau", "block/source_gem_block");
    private static final ResourceLocation GILDED_SOURCESTONE =
            new ResourceLocation("ars_nouveau", "block/gilded_sourcestone_large_bricks");

    ItemModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, ArsEnergistique.MODID, existing);
        existing.trackGenerated(P2P_TUNNEL_BASE_ITEM, MODEL);
        existing.trackGenerated(P2P_TUNNEL_BASE_PART, MODEL);
        existing.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        existing.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        existing.trackGenerated(ENERGY_ACCEPTOR_PART, MODEL);
        existing.trackGenerated(ENERGY_ACCEPTOR_PART_ITEM, MODEL);
        existing.trackGenerated(SOURCE_GEM_BLOCK, TEXTURE);
        existing.trackGenerated(GILDED_SOURCESTONE, TEXTURE);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(ArsEngItems.SOURCE_CELL_HOUSING);
        flatSingleLayer(ArsEngItems.CREATIVE_SOURCE_CELL);

        ArsEngItems.getCells().forEach(cell -> flatSingleLayer(cell).texture("layer1", STORAGE_CELL_LED));
        ArsEngItems.getPortables().forEach(cell -> flatSingleLayer(cell).texture("layer1", PORTABLE_CELL_LED));

        withExistingParent("item/source_p2p_tunnel", P2P_TUNNEL_BASE_ITEM).texture("type", SOURCE_GEM_BLOCK);
        withExistingParent("part/source_p2p_tunnel", P2P_TUNNEL_BASE_PART).texture("type", SOURCE_GEM_BLOCK);
        withExistingParent("item/spell_p2p_tunnel", P2P_TUNNEL_BASE_ITEM).texture("type", GILDED_SOURCESTONE);
        withExistingParent("part/spell_p2p_tunnel", P2P_TUNNEL_BASE_PART).texture("type", GILDED_SOURCESTONE);

        withExistingParent("part/source_acceptor", ENERGY_ACCEPTOR_PART)
                .texture("sides", "part/source_acceptor_sides")
                .texture("back", "part/source_acceptor_back")
                .texture("front", "block/source_acceptor")
                .texture("particle", "part/source_acceptor_back");
        withExistingParent("item/cable_source_acceptor", ENERGY_ACCEPTOR_PART_ITEM)
                .texture("sides", "part/source_acceptor_sides")
                .texture("back", "part/source_acceptor_back")
                .texture("front", "block/source_acceptor");
    }

    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item) {
        var path = item.id().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", ArsEnergistique.makeId("item/" + path));
    }
}
