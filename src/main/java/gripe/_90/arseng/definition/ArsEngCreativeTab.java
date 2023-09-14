package gripe._90.arseng.definition;

import java.util.ArrayList;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegisterEvent;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

import gripe._90.arseng.ArsEnergistique;

public final class ArsEngCreativeTab {
    private ArsEngCreativeTab() {}

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    ArsEnergistique.makeId("tab"),
                    CreativeModeTab.builder()
                            .title(Component.literal("Ars Énergistique"))
                            .icon(ArsEngItems.SOURCE_CELL_256K::stack)
                            .displayItems(ArsEngCreativeTab::display)
                            .build());
        }
    }

    private static void display(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var defs = new ArrayList<ItemDefinition<?>>();
        defs.addAll(ArsEngItems.getItems());
        defs.addAll(ArsEngBlocks.getBlocks());

        for (var def : defs) {
            var item = def.asItem();

            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(output);
            } else {
                output.accept(def);
            }
        }
    }
}
