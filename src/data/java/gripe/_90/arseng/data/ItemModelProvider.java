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

    private static final ResourceLocation SOURCE_GEM_BLOCK =
            new ResourceLocation("ars_nouveau", "blocks/source_gem_block");

    public ItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, ArsEngCore.MODID, efh);
        efh.trackGenerated(P2P_TUNNEL_BASE_ITEM, MODEL);
        efh.trackGenerated(P2P_TUNNEL_BASE_PART, MODEL);
        efh.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        efh.trackGenerated(SOURCE_GEM_BLOCK, TEXTURE);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(ArsEngItems.SOURCE_CELL_COMPONENT); // TODO: Texture is currently a placeholder from MEGA Cells
        flatSingleLayer(ArsEngItems.SOURCE_STORAGE_CELL).texture("layer1", STORAGE_CELL_LED);

        withExistingParent("item/source_p2p_tunnel", P2P_TUNNEL_BASE_ITEM).texture("type", SOURCE_GEM_BLOCK);
        withExistingParent("part/source_p2p_tunnel", P2P_TUNNEL_BASE_PART).texture("type", SOURCE_GEM_BLOCK);
    }

    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item) {
        var path = item.id().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", ArsEngCore.makeId("item/" + path));
    }
}
