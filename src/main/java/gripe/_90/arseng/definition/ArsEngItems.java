package gripe._90.arseng.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;

import gripe._90.arseng.item.SourceCellItem;

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

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, ArsEngCore.makeId(id), factory.apply(new Item.Properties()));
        ITEMS.add(definition);
        return definition;
    }
}
