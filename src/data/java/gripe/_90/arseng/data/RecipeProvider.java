package gripe._90.arseng.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import appeng.core.definitions.AEBlocks;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngItems;

class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        for (var cell : ArsEngItems.getCells()) {
            var tier = cell.asItem().getTier();
            var prefix = tier.namePrefix();
            var component = tier.componentSupplier().get();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                    .requires(ArsEngItems.SOURCE_CELL_HOUSING)
                    .requires(component)
                    .unlockedBy("has_source_cell_housing", has(ArsEngItems.SOURCE_CELL_HOUSING))
                    .unlockedBy("has_cell_component_" + prefix, has(component))
                    .save(output, cell.id());
        }

        for (var portable : ArsEngItems.getPortables()) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, portable)
                    .requires(AEBlocks.ME_CHEST)
                    .requires(portable.asItem().getTier().componentSupplier().get())
                    .requires(AEBlocks.ENERGY_CELL)
                    .requires(ArsEngItems.SOURCE_CELL_HOUSING)
                    .unlockedBy("has_source_cell_housing", has(ArsEngItems.SOURCE_CELL_HOUSING))
                    .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
                    .save(output, portable.id());
        }

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ArsEngItems.SOURCE_ACCEPTOR_PART)
                .requires(ArsEngBlocks.SOURCE_CONVERTER)
                .unlockedBy("has_source_acceptor", has(ArsEngBlocks.SOURCE_CONVERTER))
                .save(output, ArsEnergistique.makeId("cable_source_acceptor"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ArsEngBlocks.SOURCE_CONVERTER)
                .requires(ArsEngItems.SOURCE_ACCEPTOR_PART)
                .unlockedBy("has_source_acceptor", has(ArsEngBlocks.SOURCE_CONVERTER))
                .save(output, ArsEnergistique.makeId("source_acceptor_from_part"));
    }
}
