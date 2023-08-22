package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.ApparatusPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.CraftingPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.IPatchouliPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.PatchouliBuilder;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.TextPage;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngItems;

import java.nio.file.Path;

@SuppressWarnings("deprecation")
public class DocumentationProvider extends PatchouliProvider {
    public DocumentationProvider(DataGenerator generator) {
        super(generator);
    }


    @Override
    public PatchouliBuilder buildBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        var builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage(
                        "arseng.page." + Registry.ITEM.getKey(item.asItem()).getPath()));

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
    public Path getPath(ResourceLocation category, ResourceLocation fileName) {
        return this.generator.getOutputFolder().resolve("data/arseng/patchouli_books/worn_notebook/en_us/entries/" + category.getPath() + "/" + fileName.getPath() + ".json");
    }

    @Override
    public Path getPath(ResourceLocation category, String fileName) {
        return this.generator.getOutputFolder().resolve("data/arseng/patchouli_books/worn_notebook/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
    }

    @Override
    public void addEntries() {
        var sourceAcceptorBuilder = buildBasicItem(
                ArsEngBlocks.SOURCE_ACCEPTOR, AUTOMATION, new ApparatusPage(ArsEngBlocks.SOURCE_ACCEPTOR));
        sourceAcceptorBuilder.withPage(new TextPage(getLangPath("source_acceptor_description")));
        sourceAcceptorBuilder.withPage(new CraftingPage("arseng:source_acceptor_part"));//name must match that of the recipe
        addPage(new PatchouliPage(
                sourceAcceptorBuilder,
                getPath(AUTOMATION, Registry.ITEM.getKey(ArsEngBlocks.SOURCE_ACCEPTOR.asItem()))));

        var cellsBuilder = buildBasicItem(
                ArsEngItems.SOURCE_CELL_HOUSING, AUTOMATION, new ApparatusPage(ArsEngItems.SOURCE_CELL_HOUSING));
        ArsEngItems.getCells().forEach(cell -> cellsBuilder.withPage(new CraftingPage(cell)));
        addPage(new PatchouliPage(cellsBuilder, getPath(AUTOMATION, "me_cells")));
    }
}
