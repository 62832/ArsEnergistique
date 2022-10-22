package gripe._90.arseng.data;

import net.minecraftforge.data.event.GatherDataEvent;

public class ArsEngDataGenerator {
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var efh = event.getExistingFileHelper();

        gen.addProvider(true, new RecipeProvider(gen));
        gen.addProvider(true, new ItemModelProvider(gen, efh));
        gen.addProvider(true, new BlockModelProvider(gen, efh));
    }
}
