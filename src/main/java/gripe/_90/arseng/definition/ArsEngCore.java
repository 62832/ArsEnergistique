package gripe._90.arseng.definition;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ArsEngCore {
    private ArsEngCore() {}

    public static final String MODID = "arseng";

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MODID, id);
    }

    static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(ArsEngCore.MODID) {
        @NotNull
        @Override
        public ItemStack makeIcon() {
            return ArsEngItems.SOURCE_CELL_256K.stack();
        }
    };
}
