package gripe._90.arseng.data;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.definition.ArsEngItems;

class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    RecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ArsEngItems.SOURCE_STORAGE_CELL)
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', AEBlocks.QUARTZ_GLASS)
                .define('b', Tags.Items.DUSTS_REDSTONE)
                .define('c', ArsEngItems.SOURCE_CELL_COMPONENT)
                .define('d', Tags.Items.INGOTS_GOLD)
                .define('e', ItemsRegistry.SOURCE_GEM)
                .unlockedBy("has_source_cell_component", has(ArsEngItems.SOURCE_CELL_COMPONENT))
                .save(consumer, ArsEngItems.SOURCE_STORAGE_CELL.id());
    }
}
