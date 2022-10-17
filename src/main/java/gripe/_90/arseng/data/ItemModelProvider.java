package gripe._90.arseng.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import appeng.core.AppEng;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.item.ArsEngItems;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");

    public ItemModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, ArsEnergistique.MODID, efh);
        efh.trackGenerated(STORAGE_CELL_LED, TEXTURE);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(ArsEngItems.SOURCE_CELL_COMPONENT); // TODO: Texture is currently a placeholder from MEGA Cells
        flatSingleLayer(ArsEngItems.SOURCE_STORAGE_CELL).texture("layer1", STORAGE_CELL_LED);
    }

    private ItemModelBuilder flatSingleLayer(RegistryObject<Item> item) {
        String path = item.getId().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", ArsEnergistique.makeId("item/" + path));
    }
}
