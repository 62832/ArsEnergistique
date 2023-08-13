package gripe._90.arseng.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import gripe._90.arseng.definition.ArsEngCore;

@Mod.EventBusSubscriber(modid = ArsEngCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ArsEngDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var efh = event.getExistingFileHelper();

        gen.addProvider(true, new ItemModelProvider(gen, efh));
        gen.addProvider(true, new BlockModelProvider(gen, efh));

        gen.addProvider(true, new RecipeProvider(gen));
        gen.addProvider(true, new LocalisationProvider(gen));
    }
}
