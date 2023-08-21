package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;
import appeng.core.definitions.ItemDefinition;

import gripe._90.arseng.definition.ArsEngCore;
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

    ItemModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, ArsEngCore.MODID, existing);
        existing.trackGenerated(P2P_TUNNEL_BASE_ITEM, MODEL);
        existing.trackGenerated(P2P_TUNNEL_BASE_PART, MODEL);
        existing.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        existing.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        existing.trackGenerated(ENERGY_ACCEPTOR_PART, MODEL);
        existing.trackGenerated(ENERGY_ACCEPTOR_PART_ITEM, MODEL);
        existing.trackGenerated(SOURCE_GEM_BLOCK, TEXTURE);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(ArsEngItems.SOURCE_CELL_HOUSING);
        flatSingleLayer(ArsEngItems.CREATIVE_SOURCE_CELL);

        ArsEngItems.getCells().forEach(cell -> flatSingleLayer(cell).texture("layer1", STORAGE_CELL_LED));
        ArsEngItems.getPortables().forEach(portable -> {
            var side = AppEng.makeId(
                    "item/portable_cell_side_" + portable.asItem().getTier().namePrefix());
            existingFileHelper.trackGenerated(side, TEXTURE);

            withExistingParent(portable.id().getPath(), mcLoc("item/generated"))
                    .texture("layer0", ArsEngCore.makeId("item/portable_cell_screen"))
                    .texture("layer1", PORTABLE_CELL_LED)
                    .texture("layer2", ArsEngCore.makeId("item/portable_cell_housing"))
                    .texture("layer3", side);
        });

        withExistingParent("item/source_p2p_tunnel", P2P_TUNNEL_BASE_ITEM).texture("type", SOURCE_GEM_BLOCK);
        withExistingParent("part/source_p2p_tunnel", P2P_TUNNEL_BASE_PART).texture("type", SOURCE_GEM_BLOCK);

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
        return singleTexture(path, mcLoc("item/generated"), "layer0", ArsEngCore.makeId("item/" + path));
    }
}
