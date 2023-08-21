package gripe._90.arseng.data;

import java.io.IOException;

import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.Items;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

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
                .withResult(ArsEngBlocks.SOURCE_ACCEPTOR)
                .withReagent(AEBlocks.ENERGY_ACCEPTOR)
                .withPedestalItem(4, BlockRegistry.SOURCE_GEM_BLOCK)
                .withPedestalItem(4, Items.GOLD_BLOCK)
                .withSourceCost(10000)
                .withId(ArsEngBlocks.SOURCE_ACCEPTOR.id())
                .build());
    }

    @Override
    public void run(CachedOutput pOutput) throws IOException {
        addEntries();

        for (var recipe : recipes) {
            DataProvider.saveStable(
                    pOutput,
                    recipe.asRecipe(),
                    getRecipePath(
                            this.generator.getOutputFolder(), recipe.getId().getPath()));
        }
    }

    @Override
    public String getName() {
        return "Apparatus Recipes";
    }
}
