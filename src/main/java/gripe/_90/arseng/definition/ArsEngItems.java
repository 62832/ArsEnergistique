package gripe._90.arseng.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.PartModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.GuiText;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.items.storage.StorageTier;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.arseng.part.SourceConverterPart;
import gripe._90.arseng.part.SourceP2PTunnelPart;
import gripe._90.arseng.part.SpellP2PTunnelPart;

public final class ArsEngItems {
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(ArsEnergistique.MODID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
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

    public static final ItemDefinition<PartItem<SourceP2PTunnelPart>> SOURCE_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SourceP2PTunnelPart.class));
        return item(
                "ME Source P2P Tunnel",
                "source_p2p_tunnel",
                p -> new PartItem<>(p, SourceP2PTunnelPart.class, SourceP2PTunnelPart::new));
    });

    public static final ItemDefinition<PartItem<SpellP2PTunnelPart>> SPELL_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SpellP2PTunnelPart.class));
        return item(
                "ME Spell P2P Tunnel",
                "spell_p2p_tunnel",
                p -> new PartItem<>(p, SpellP2PTunnelPart.class, SpellP2PTunnelPart::new));
    });

    public static final ItemDefinition<PartItem<SourceConverterPart>> SOURCE_ACCEPTOR_PART = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SourceConverterPart.class));
        return item(
                "ME Source Converter",
                "cable_source_acceptor",
                p -> new PartItem<>(p, SourceConverterPart.class, SourceConverterPart::new));
    });

    @SuppressWarnings("CodeBlock2Expr")
    public static void initCellUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            getCells().forEach(cell -> {
                Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey());
            });
            getPortables().forEach(cell -> {
                Upgrades.add(AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
                Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
            });
        });
    }

    public static void initP2PAttunement(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            P2PTunnelAttunement.registerAttunementTag(SOURCE_P2P_TUNNEL);
            P2PTunnelAttunement.registerAttunementTag(SPELL_P2P_TUNNEL);
        });
    }

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
                p -> new PortableSourceCellItem(p.stacksTo(1), tier));
    }

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, DR.registerItem(id, factory));
        ITEMS.add(definition);
        return definition;
    }
}
