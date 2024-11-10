package gripe._90.arseng.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.Util;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import gripe._90.arseng.ArsEnergistique;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = ArsEnergistique.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ArsEngDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var pack = event.getGenerator().getVanillaPack(true);
        var existing = event.getExistingFileHelper();

        pack.addProvider(output -> new ItemModelProvider(output, existing));
        pack.addProvider(output -> new BlockStateModelProvider(output, existing));

        pack.addProvider(LocalisationProvider::new);

        var registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        var blockTags = pack.addProvider(output -> new TagsProvider.Blocks(output, registries));
        pack.addProvider(output -> new TagsProvider.Items(output, registries, blockTags.contentsGetter()));
        pack.addProvider(output -> new RecipeProvider(output, registries));

        var blockDrops = new LootTableProvider.SubProviderEntry(BlockDropProvider::new, LootContextParamSets.BLOCK);
        pack.addProvider(output -> new LootTableProvider(output, Set.of(), List.of(blockDrops), registries));
    }
}
