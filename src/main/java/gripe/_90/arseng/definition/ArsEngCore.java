package gripe._90.arseng.definition;

import net.minecraft.resources.ResourceLocation;

public final class ArsEngCore {
    private ArsEngCore() {}

    public static final String MODID = "arseng";

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MODID, id);
    }
}
