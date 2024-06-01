package gripe._90.arseng.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;

class LocalisationProvider extends LanguageProvider {
    LocalisationProvider(DataGenerator generator) {
        super(generator, ArsEngCore.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ArsEngItems.getItems().forEach(i -> add(i.asItem(), i.getEnglishName()));
        ArsEngBlocks.getBlocks().forEach(b -> add(b.block(), b.getEnglishName()));

        add("itemGroup." + ArsEngCore.MODID, "Ars Énergistique");

        add(
                "arseng.page.source_cell_housing",
                "ME Source Cells can be crafted with an ME Source Cell Housing. They will store many jars' worth of source.");
        add(
                "arseng.page.source_acceptor",
                "The ME Source Converter converts source into AE energy. Attach to an ME network and feed it source with a relay.");
        add("arseng.page.source_acceptor_description", "The Source Converter comes in both block and cable part form.");
    }
}
