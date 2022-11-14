package gripe._90.arseng.item;

import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import appeng.api.parts.PartModels;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.item.cell.SourceCellItem;
import gripe._90.arseng.part.SourceP2PTunnelPart;

public class ArsEngItems {

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }

    // spotless:off
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArsEnergistique.MODID);

    private static final Item.Properties PROPS = new Item.Properties().tab(ArsEnergistique.CREATIVE_TAB);

    public static final RegistryObject<Item> SOURCE_CELL_COMPONENT = ITEMS.register("source_cell_component", () -> new MaterialItem(PROPS));
    public static final RegistryObject<Item> SOURCE_STORAGE_CELL = ITEMS.register("source_storage_cell", () -> new SourceCellItem(PROPS.stacksTo(1)));

    public static final RegistryObject<PartItem<SourceP2PTunnelPart>> SOURCE_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(SourceP2PTunnelPart.class));
        return ITEMS.register("source_p2p_tunnel", () -> new PartItem<>(PROPS, SourceP2PTunnelPart.class, SourceP2PTunnelPart::new));
    });
    // spotless:on
}
