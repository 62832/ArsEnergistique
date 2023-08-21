package gripe._90.arseng.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import appeng.core.definitions.AEItems;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;

class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    RecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        for (var cell : ArsEngItems.getCells()) {
            var tier = cell.asItem().getTier();
            var prefix = tier.namePrefix();
            var component = tier.componentSupplier().get();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                    .requires(ArsEngItems.SOURCE_CELL_HOUSING)
                    .requires(component)
                    .unlockedBy("has_source_cell_housing", has(ArsEngItems.SOURCE_CELL_HOUSING))
                    .unlockedBy("has_cell_component_" + prefix, has(component))
                    .save(consumer, cell.id());
        }

        for (var portable : ArsEngItems.getPortables()) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, portable)
                    .requires(AEBlocks.CHEST)
                    .requires(portable.asItem().getTier().componentSupplier().get())
                    .requires(AEBlocks.ENERGY_CELL)
                    .requires(ArsEngItems.SOURCE_CELL_HOUSING)
                    .unlockedBy("has_source_cell_housing", has(ArsEngItems.SOURCE_CELL_HOUSING))
                    .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
                    .save(consumer, portable.id());
        }

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ArsEngItems.SOURCE_ACCEPTOR_PART)
                .requires(ArsEngBlocks.SOURCE_ACCEPTOR)
                .unlockedBy("has_source_acceptor", has(ArsEngBlocks.SOURCE_ACCEPTOR))
                .save(consumer, ArsEngCore.makeId("source_acceptor_part"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ArsEngBlocks.SOURCE_ACCEPTOR)
                .requires(ArsEngItems.SOURCE_ACCEPTOR_PART)
                .unlockedBy("has_source_acceptor", has(ArsEngBlocks.SOURCE_ACCEPTOR))
                .save(consumer, ArsEngCore.makeId("source_acceptor_from_part"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ArsEngItems.SOURCE_CELL_HOUSING)
                .unlockedBy("has_source_gem", has(ItemsRegistry.SOURCE_GEM))
                .pattern("qrq")
                .pattern("rsr")
                .pattern("ggg")
                .define('q',AEBlocks.QUARTZ_GLASS.asItem())
                .define('r',Items.REDSTONE)
                .define('g',Items.GOLD_INGOT)
                .define('s',ItemsRegistry.SOURCE_GEM)
                .save(consumer, ArsEngItems.SOURCE_CELL_HOUSING.id());
    }
}
