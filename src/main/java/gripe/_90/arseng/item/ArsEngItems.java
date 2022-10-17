package gripe._90.arseng.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import appeng.items.materials.MaterialItem;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.item.cell.SourceCellItem;

public class ArsEngItems {

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }

    // spotless:off
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArsEnergistique.MODID);

    private static final Item.Properties PROPS = new Item.Properties().tab(ArsEnergistique.CREATIVE_TAB);

    public static final RegistryObject<Item> SOURCE_CELL_COMPONENT = ITEMS.register("source_cell_component", () -> new MaterialItem(PROPS));
    public static final RegistryObject<Item> SOURCE_STORAGE_CELL = ITEMS.register("source_storage_cell", () -> new SourceCellItem(PROPS.stacksTo(1)));
    // spotless:on
}
