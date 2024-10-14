package gripe._90.arseng.data;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.ApparatusPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.CraftingPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.IPatchouliPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.PatchouliBuilder;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.TextPage;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngItems;

public class DocumentationProvider extends PatchouliProvider {
    public DocumentationProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public PatchouliBuilder buildBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        var builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage(
                        "arseng.page." + getRegistryName(item.asItem()).getPath()));

        if (recipePage != null) {
            builder.withPage(recipePage);
        }

        return builder;
    }

    @Override
    public String getLangPath(String name) {
        return "arseng.page." + name;
    }

    @Override
    public void addEntries() {
        var sourceAcceptorBuilder = buildBasicItem(
                ArsEngBlocks.SOURCE_CONVERTER, AUTOMATION, new ApparatusPage(ArsEngBlocks.SOURCE_CONVERTER));
        sourceAcceptorBuilder.withPage(new TextPage(getLangPath("source_acceptor_description")));
        sourceAcceptorBuilder.withPage(new CraftingPage(ArsEngItems.SOURCE_ACCEPTOR_PART));
        addPage(new PatchouliPage(
                sourceAcceptorBuilder, getPath(AUTOMATION, getRegistryName(ArsEngBlocks.SOURCE_CONVERTER.asItem()))));

        var cellsBuilder = buildBasicItem(
                ArsEngItems.SOURCE_CELL_HOUSING, AUTOMATION, new ApparatusPage(ArsEngItems.SOURCE_CELL_HOUSING));
        ArsEngItems.getCells().forEach(cell -> cellsBuilder.withPage(new CraftingPage(cell)));
        addPage(new PatchouliPage(cellsBuilder, getPath(AUTOMATION, "me_cells")));
    }
}
