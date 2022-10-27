package gripe._90.arseng;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.items.storage.BasicStorageCell;
import appeng.parts.automation.StackWorldBehaviors;

import gripe._90.arseng.ae2.SourceContainerItemStrategy;
import gripe._90.arseng.ae2.SourceKey;
import gripe._90.arseng.ae2.SourceKeyType;
import gripe._90.arseng.ae2.client.SourceRenderer;
import gripe._90.arseng.ae2.stack.SourceExportStrategy;
import gripe._90.arseng.ae2.stack.SourceExternalStorageStrategy;
import gripe._90.arseng.ae2.stack.SourceImportStrategy;
import gripe._90.arseng.data.ArsEngDataGenerator;
import gripe._90.arseng.item.ArsEngItems;
import gripe._90.arseng.item.cell.SourceCellHandler;

@Mod(ArsEnergistique.MODID)
@SuppressWarnings("UnstableApiUsage")
public class ArsEnergistique {

    public static final String MODID = "arseng";

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ArsEngItems.SOURCE_STORAGE_CELL.get());
        }
    };

    public ArsEnergistique() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ArsEngItems.init(bus);

        bus.addListener(ArsEngDataGenerator::onGatherData);

        bus.addListener((RegisterEvent event) -> {
            if (!event.getRegistryKey().equals(Registry.ITEM_REGISTRY)) {
                return;
            }

            AEKeyTypes.register(SourceKeyType.TYPE);
        });

        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(this::registerCell));

        StackWorldBehaviors.registerImportStrategy(SourceKeyType.TYPE, SourceImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SourceKeyType.TYPE, SourceExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(SourceKeyType.TYPE, SourceExternalStorageStrategy::new);

        ContainerItemStrategy.register(SourceKeyType.TYPE, SourceKey.class, new SourceContainerItemStrategy());
        GenericSlotCapacities.register(SourceKeyType.TYPE, GenericSlotCapacities.getMap().get(AEKeyType.fluids()));

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::init);
    }

    private void registerCell() {
        StorageCells.addCellHandler(SourceCellHandler.INSTANCE);
        StorageCellModels.registerModel(
                ArsEngItems.SOURCE_STORAGE_CELL.get(),
                makeId("block/drive/cells/source_storage_cell"));
    }

    private static class ClientSetup {
        static void init() {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();

            bus.addListener(ClientSetup::registerItemColors);
            SourceRenderer.init(bus);
        }

        private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(BasicStorageCell::getColor, ArsEngItems.SOURCE_STORAGE_CELL.get());
        }
    }
}
