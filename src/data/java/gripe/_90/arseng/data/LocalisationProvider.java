package gripe._90.arseng.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.key.SourceKeyType;

class LocalisationProvider extends LanguageProvider {
    public LocalisationProvider(DataGenerator gen) {
        super(gen, ArsEngCore.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.arseng", "Ars Énergistique");
        add(SourceKeyType.SOURCE.getString(), "Source");

        ArsEngItems.getItems().forEach(i -> add(i.asItem(), i.getEnglishName()));
    }
}
