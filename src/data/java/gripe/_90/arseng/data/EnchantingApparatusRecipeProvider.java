package gripe._90.arseng.data;

import appeng.core.definitions.AEBlocks;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import gripe._90.arseng.definition.ArsEngBlocks;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;

import java.nio.file.Path;

public class EnchantingApparatusRecipeProvider extends ApparatusRecipeProvider {
    public EnchantingApparatusRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void addEntries() {
        addRecipe(builder()
                .withResult(ArsEngBlocks.SOURCE_ACCEPTOR.asItem())
                .withReagent(AEBlocks.ENERGY_ACCEPTOR.asItem())
                .withPedestalItem(4, ItemsRegistry.SOURCE_GEM)
                .withPedestalItem(4, Items.GOLD_INGOT)
                .withSourceCost(10000)
                .withId(ArsEngBlocks.SOURCE_ACCEPTOR.id())
                .build());
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (IEnchantingRecipe g : recipes) {
            if (g instanceof EnchantingApparatusRecipe recipe) {
                Path path = getRecipePath(output, recipe.getId().getPath());
                saveStable(pOutput, recipe.asRecipe(), path);
            }
        }
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/arseng/recipes/" + str + ".json");
    }

    @Override
    public String getName() {
        return "Ars Eng Apparatus Recipes";
    }


}
