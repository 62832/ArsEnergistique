package gripe._90.arseng.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

import gripe._90.arseng.item.SourceCellItem;
import gripe._90.arseng.part.SourceP2PTunnelPart;

public final class ArsEngItems {
    private ArsEngItems() {}

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            ITEMS.forEach(i -> ForgeRegistries.ITEMS.register(i.id(), i.asItem()));
        }
    }

    public static final ItemDefinition<MaterialItem> SOURCE_CELL_COMPONENT =
            item("ME Source Cell Component", "source_cell_component", MaterialItem::new);
    public static final ItemDefinition<SourceCellItem> SOURCE_STORAGE_CELL =
            item("ME Source Storage Cell", "source_storage_cell", p -> new SourceCellItem(p.stacksTo(1)));

    public static final ItemDefinition<PartItem<SourceP2PTunnelPart>> SOURCE_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SourceP2PTunnelPart.class));
        return item(
                "ME Source P2P Tunnel",
                "source_p2p_tunnel",
                p -> new PartItem<>(p, SourceP2PTunnelPart.class, SourceP2PTunnelPart::new));
    });

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, ArsEngCore.makeId(id), factory.apply(new Item.Properties()));
        ITEMS.add(definition);
        return definition;
    }
}
