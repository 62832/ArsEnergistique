package gripe._90.arseng.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.arseng.definition.ArsEngCore;

@Mod.EventBusSubscriber(modid = ArsEngCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ArsEngDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var pack = event.getGenerator().getVanillaPack(true);
        var existing = event.getExistingFileHelper();

        pack.addProvider(output -> new ItemModelProvider(output, existing));
        pack.addProvider(output -> new BlockStateModelProvider(output, existing));

        pack.addProvider(RecipeProvider::new);
        pack.addProvider(LocalisationProvider::new);

        pack.addProvider(g -> new DocumentationProvider(event.getGenerator()));
        pack.addProvider(g -> new EnchantingRecipeProvider(event.getGenerator()));

        var registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        var blockTags = pack.addProvider(output -> new BlockTagsProvider(output, registries, ArsEngCore.MODID, null) {
            @Override
            protected void addTags(@NotNull HolderLookup.Provider provider) {}
        });
        pack.addProvider(output -> new TagsProvider(output, registries, blockTags.contentsGetter()));

        var blockDrops = new LootTableProvider.SubProviderEntry(BlockDropProvider::new, LootContextParamSets.BLOCK);
        pack.addProvider(output -> new LootTableProvider(output, Set.of(), List.of(blockDrops)));
    }
}
