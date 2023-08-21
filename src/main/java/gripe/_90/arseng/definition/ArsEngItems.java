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
import appeng.items.storage.StorageTier;

import gripe._90.arseng.item.CreativeSourceCellItem;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.arseng.part.SourceAcceptorPart;
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

    public static final ItemDefinition<MaterialItem> SOURCE_CELL_HOUSING =
            item("ME Source Cell Housing", "source_cell_housing", MaterialItem::new);

    // spotless:off
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_1K = cell(StorageTier.SIZE_1K);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_4K = cell(StorageTier.SIZE_4K);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_16K = cell(StorageTier.SIZE_16K);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_64K = cell(StorageTier.SIZE_64K);
    public static final ItemDefinition<SourceCellItem> SOURCE_CELL_256K = cell(StorageTier.SIZE_256K);

    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL1K = portable(StorageTier.SIZE_1K);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL4K = portable(StorageTier.SIZE_4K);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL16K = portable(StorageTier.SIZE_16K);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL64K = portable(StorageTier.SIZE_64K);
    public static final ItemDefinition<PortableSourceCellItem> PORTABLE_SOURCE_CELL256K = portable(StorageTier.SIZE_256K);
    // spotless:on

    public static final ItemDefinition<CreativeSourceCellItem> CREATIVE_SOURCE_CELL =
            item("Creative ME Source Cell", "creative_source_cell", CreativeSourceCellItem::new);

    public static final ItemDefinition<PartItem<SourceP2PTunnelPart>> SOURCE_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SourceP2PTunnelPart.class));
        return item(
                "ME Source P2P Tunnel",
                "source_p2p_tunnel",
                p -> new PartItem<>(p, SourceP2PTunnelPart.class, SourceP2PTunnelPart::new));
    });

    public static final ItemDefinition<PartItem<SourceAcceptorPart>> SOURCE_ACCEPTOR_PART = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SourceAcceptorPart.class));
        return item(
                "ME Source Acceptor",
                "cable_source_acceptor",
                p -> new PartItem<>(p, SourceAcceptorPart.class, SourceAcceptorPart::new));
    });

    public static List<ItemDefinition<SourceCellItem>> getCells() {
        return List.of(SOURCE_CELL_1K, SOURCE_CELL_4K, SOURCE_CELL_16K, SOURCE_CELL_64K, SOURCE_CELL_256K);
    }

    public static List<ItemDefinition<PortableSourceCellItem>> getPortables() {
        return List.of(
                PORTABLE_SOURCE_CELL1K,
                PORTABLE_SOURCE_CELL4K,
                PORTABLE_SOURCE_CELL16K,
                PORTABLE_SOURCE_CELL64K,
                PORTABLE_SOURCE_CELL256K);
    }

    private static ItemDefinition<SourceCellItem> cell(StorageTier tier) {
        return item(
                tier.namePrefix() + " ME Source Storage Cell",
                "source_storage_cell_" + tier.namePrefix(),
                p -> new SourceCellItem(p.stacksTo(1), tier));
    }

    private static ItemDefinition<PortableSourceCellItem> portable(StorageTier tier) {
        return item(
                tier.namePrefix() + " Portable Source Cell",
                "portable_source_cell_" + tier.namePrefix(),
                p -> new PortableSourceCellItem(p.stacksTo(1), tier, 0xFAD64A));
    }

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, ArsEngCore.makeId(id), factory.apply(new Item.Properties()));
        ITEMS.add(definition);
        return definition;
    }
}
