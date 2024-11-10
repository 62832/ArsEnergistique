package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngItems;

class LocalisationProvider extends LanguageProvider {
    LocalisationProvider(PackOutput output) {
        super(output, ArsEnergistique.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ArsEngItems.getItems().forEach(i -> add(i.asItem(), i.getEnglishName()));
        ArsEngBlocks.getBlocks().forEach(b -> add(b.block(), b.getEnglishName()));
        add("itemGroup." + ArsEnergistique.MODID, "Ars Énergistique");
    }
}
