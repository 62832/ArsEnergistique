package gripe._90.arseng;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.AEStackRendering;
import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.parts.automation.StackWorldBehaviors;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.definition.ArsEngConfig;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.me.cell.CreativeSourceCellHandler;
import gripe._90.arseng.me.cell.SourceCellHandler;
import gripe._90.arseng.me.client.SourceRenderer;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;
import gripe._90.arseng.me.strategy.SourceContainerItemStrategy;
import gripe._90.arseng.me.strategy.SourceExternalStorageStrategy;
import gripe._90.arseng.me.strategy.SourceStorageExportStrategy;
import gripe._90.arseng.me.strategy.SourceStorageImportStrategy;
import gripe._90.arseng.part.SpellP2PTunnelPart;

@Mod(ArsEngCore.MODID)
public class ArsEnergistique {
    @SuppressWarnings("UnstableApiUsage")
    public ArsEnergistique() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArsEngConfig.SPEC);

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ArsEngItems::register);
        bus.addListener(ArsEngBlocks::register);

        bus.addListener(SourceKeyType::register);
        StorageCells.addCellHandler(SourceCellHandler.INSTANCE);
        StorageCells.addCellHandler(CreativeSourceCellHandler.INSTANCE);

        ArsEngItems.getCells()
                .forEach(cell -> Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey()));
        ArsEngItems.getPortables().forEach(cell -> {
            Upgrades.add(AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
        });

        bus.addListener(ArsEngCapabilities::register);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, ArsEngCapabilities::attach);

        StackWorldBehaviors.registerImportStrategy(SourceKeyType.TYPE, SourceStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SourceKeyType.TYPE, SourceStorageExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(SourceKeyType.TYPE, SourceExternalStorageStrategy::new);

        ContainerItemStrategy.register(SourceKeyType.TYPE, SourceKey.class, SourceContainerItemStrategy.INSTANCE);
        GenericSlotCapacities.register(SourceKeyType.TYPE, (long) SourceContainerItemStrategy.MAX_SOURCE);

        bus.addListener(ArsEngItems::initP2PAttunement);
        MinecraftForge.EVENT_BUS.addListener(SpellP2PTunnelPart::onSpellHit);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            Client.init();
        }
    }

    private static class Client {
        private static void init() {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(SourceCellHandler::initLED);
            bus.addListener(PortableSourceCellItem::initColours);

            AEStackRendering.register(SourceKeyType.TYPE, SourceKey.class, SourceRenderer.INSTANCE);

            var driveCell = ArsEngCore.makeId("block/source_drive_cell");
            ArsEngItems.getCells().forEach(cell -> StorageCellModels.registerModel(cell, driveCell));
            ArsEngItems.getPortables().forEach(portable -> StorageCellModels.registerModel(portable, driveCell));
            StorageCellModels.registerModel(ArsEngItems.CREATIVE_SOURCE_CELL, driveCell);
        }
    }
}
