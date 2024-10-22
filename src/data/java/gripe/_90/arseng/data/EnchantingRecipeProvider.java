package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngItems;

public class EnchantingRecipeProvider extends ApparatusRecipeProvider {
    public EnchantingRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void addEntries() {
        addRecipe(builder()
                .withResult(ArsEngItems.SOURCE_CELL_HOUSING)
                .withReagent(AEItems.ITEM_CELL_HOUSING)
                .withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(2, ItemsRegistry.SOURCE_GEM)
                .withPedestalItem(4, Items.GOLD_INGOT)
                .build());
        addRecipe(builder()
                .withResult(ArsEngBlocks.SOURCE_CONVERTER)
                .withReagent(AEBlocks.ENERGY_ACCEPTOR)
                .withPedestalItem(4, BlockRegistry.SOURCE_GEM_BLOCK)
                .withPedestalItem(4, Items.GOLD_BLOCK)
                .withSourceCost(10000)
                .build());
        addRecipe(builder()
                .withResult(ArsEngBlocks.ME_SOURCE_JAR)
                .withReagent(BlockRegistry.SOURCE_JAR)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(Ingredient.of(ConventionTags.INTERFACE))
                .build());
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();

        for (var recipe : recipes) {
            saveStable(
                    pOutput,
                    recipe.serialize(),
                    output.resolve("data/arseng/recipe/" + recipe.id().getPath() + ".json"));
        }
    }

    @Override
    public String getName() {
        return "Apparatus Recipes";
    }
}
