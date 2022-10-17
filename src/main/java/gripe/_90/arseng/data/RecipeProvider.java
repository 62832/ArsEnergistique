package gripe._90.arseng.data;

import java.util.function.Consumer;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.item.ArsEngItems;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ArsEngItems.SOURCE_STORAGE_CELL.get())
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', AEBlocks.QUARTZ_GLASS)
                .define('b', Tags.Items.DUSTS_REDSTONE)
                .define('c', ArsEngItems.SOURCE_CELL_COMPONENT.get())
                .define('d', Tags.Items.INGOTS_GOLD)
                .define('e', ItemsRegistry.SOURCE_GEM)
                .unlockedBy("has_source_cell_component", has(ArsEngItems.SOURCE_CELL_COMPONENT.get()))
                .save(consumer, ArsEngItems.SOURCE_STORAGE_CELL.getId());
    }
}
