package gripe._90.arseng.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.arseng.definition.ArsEngCore;

@Mod.EventBusSubscriber(modid = ArsEngCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ArsEngDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existing = event.getExistingFileHelper();

        generator.addProvider(true, new ItemModelProvider(generator, existing));
        generator.addProvider(true, new BlockStateModelProvider(generator, existing));

        var blockTags = new TagsProvider.Blocks(generator, existing);
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new TagsProvider.Items(generator, blockTags, existing));

        generator.addProvider(true, new RecipeProvider(generator));
        generator.addProvider(true, new LocalisationProvider(generator));
        generator.addProvider(true, new BlockDropProvider(generator));

        generator.addProvider(true, new DocumentationProvider(generator));
        generator.addProvider(true, new EnchantingRecipeProvider(generator));
    }
}
