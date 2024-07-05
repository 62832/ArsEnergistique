package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.api.recipe.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.common.datagen.BuddingConversionProvider;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.ArsEnergistique;

public class GolemRecipeProvider extends BuddingConversionProvider {
    public GolemRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void addEntries() {
        recipes.add(new BuddingConversionRecipe(
                ArsEnergistique.makeId("flawed_budding_quartz"),
                AEBlocks.QUARTZ_BLOCK.block(),
                AEBlocks.FLAWED_BUDDING_QUARTZ.block()));
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();

        for (var recipe : recipes) {
            saveStable(
                    pOutput,
                    recipe.asRecipe(),
                    output.resolve("data/arseng/recipes/" + recipe.getId().getPath() + ".json"));
        }
    }

    @Override
    public String getName() {
        return "Budding Conversion Recipes";
    }
}
