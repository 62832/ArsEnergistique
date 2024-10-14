package gripe._90.arseng.definition;

import java.util.ArrayList;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

import gripe._90.arseng.ArsEnergistique;

public final class ArsEngCreativeTab {
    public static final DeferredRegister<CreativeModeTab> DR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArsEnergistique.MODID);

    static {
        DR.register("tab", () -> CreativeModeTab.builder()
                .title(Component.literal("Ars Énergistique"))
                .icon(ArsEngItems.SOURCE_CELL_256K::stack)
                .displayItems(ArsEngCreativeTab::display)
                .build());
    }

    private static void display(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var defs = new ArrayList<ItemDefinition<?>>();
        defs.addAll(ArsEngItems.getItems());
        defs.addAll(ArsEngBlocks.getBlocks().stream().map(BlockDefinition::item).toList());

        for (var def : defs) {
            var item = def.asItem();

            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(params, output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(params, output);
            } else {
                output.accept(def);
            }
        }
    }
}
