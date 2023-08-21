package gripe._90.arseng.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;

class LocalisationProvider extends LanguageProvider {
    LocalisationProvider(PackOutput output) {
        super(output, ArsEngCore.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ArsEngItems.getItems().forEach(i -> add(i.asItem(), i.getEnglishName()));
        ArsEngBlocks.getBlocks().forEach(b -> add(b.block(), b.getEnglishName()));
        add("arseng.page.source_cell_housing","ME source cells can be crafted with ME source cell housing. They will store many jars worth of source. ");
        add("arseng.page.source_acceptor","The ME Source Acceptor converts source into AE2 energy. Attach to an AE2 network and feed it source with a relay. ");
        add("arseng.page.source_acceptor_description","There are both facade and full block versions of the Source Acceptor. ");
    }
}
