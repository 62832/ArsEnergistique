package gripe._90.arseng.data;

import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ArsEngDataGenerator {
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var efh = event.getExistingFileHelper();

        gen.addProvider(new RecipeProvider(gen));
        gen.addProvider(new ItemModelProvider(gen, efh));
        gen.addProvider(new BlockModelProvider(gen, efh));
    }
}
