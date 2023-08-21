package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class DocumentationProvider extends PatchouliProvider {
    public DocumentationProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public PatchouliBuilder buildBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("arseng.page." + getRegistryName(item.asItem()).getPath()));
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
        //obv don't call super here lol
        PatchouliBuilder sourceAcceptorBuilder = buildBasicItem(ArsEngBlocks.SOURCE_ACCEPTOR,AUTOMATION,new ApparatusPage(ArsEngBlocks.SOURCE_ACCEPTOR));
        sourceAcceptorBuilder.withPage(new TextPage(getLangPath("source_acceptor_description")));
        sourceAcceptorBuilder.withPage(new CraftingPage(ArsEngItems.SOURCE_ACCEPTOR_PART));
        addPage(new PatchouliPage(sourceAcceptorBuilder,getPath(AUTOMATION, getRegistryName(ArsEngBlocks.SOURCE_ACCEPTOR.asItem()))));

        PatchouliBuilder meCellsBuilder = buildBasicItem(ArsEngItems.SOURCE_CELL_HOUSING,AUTOMATION,new CraftingPage(ArsEngItems.SOURCE_CELL_HOUSING));
        ArsEngItems.getCells().forEach(cell -> {
            meCellsBuilder.withPage(new CraftingPage(cell));
        });
        addPage(new PatchouliPage(meCellsBuilder,getPath(AUTOMATION, "me_cells")));
    }

}
