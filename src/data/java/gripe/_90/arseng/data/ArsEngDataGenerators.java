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
        var pack = event.getGenerator().getVanillaPack(true);
        var existing = event.getExistingFileHelper();

        pack.addProvider(output -> new ItemModelProvider(output, existing));
        pack.addProvider(output -> new BlockModelProvider(output, existing));

        pack.addProvider(RecipeProvider::new);
        pack.addProvider(LocalisationProvider::new);
    }
}
