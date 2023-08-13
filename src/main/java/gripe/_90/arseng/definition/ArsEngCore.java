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

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MODID) {
        @NotNull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ArsEngItems.SOURCE_STORAGE_CELL);
        }
    };
}
