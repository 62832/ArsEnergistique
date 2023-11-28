package gripe._90.arseng;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.parts.automation.StackWorldBehaviors;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCapabilities;
import gripe._90.arseng.definition.ArsEngConfig;
import gripe._90.arseng.definition.ArsEngCreativeTab;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
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

@Mod(ArsEnergistique.MODID)
public class ArsEnergistique {
    public static final String MODID = "arseng";

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MODID, id);
    }

    private static final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    @SuppressWarnings("UnstableApiUsage")
    public ArsEnergistique() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArsEngConfig.SPEC);

        modEventBus.addListener(ArsEngItems::register);
        modEventBus.addListener(ArsEngBlocks::register);
        modEventBus.addListener(ArsEngCreativeTab::register);

        modEventBus.addListener(SourceKeyType::register);
        StorageCells.addCellHandler(SourceCellHandler.INSTANCE);
        StorageCells.addCellHandler(CreativeSourceCellHandler.INSTANCE);

        ArsEngItems.getCells()
                .forEach(cell -> Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey()));
        ArsEngItems.getPortables().forEach(cell -> {
            Upgrades.add(AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
        });

        modEventBus.addListener(ArsEngCapabilities::register);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, ArsEngCapabilities::attach);

        StackWorldBehaviors.registerImportStrategy(SourceKeyType.TYPE, SourceStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SourceKeyType.TYPE, SourceStorageExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(SourceKeyType.TYPE, SourceExternalStorageStrategy::new);

        ContainerItemStrategy.register(SourceKeyType.TYPE, SourceKey.class, SourceContainerItemStrategy.INSTANCE);
        GenericSlotCapacities.register(SourceKeyType.TYPE, (long) SourceContainerItemStrategy.MAX_SOURCE);

        modEventBus.addListener(ArsEngItems::initP2PAttunement);
        MinecraftForge.EVENT_BUS.addListener(SpellP2PTunnelPart::onSpellHit);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            Client.init();
        }
    }

    private static class Client {
        private static void init() {
            modEventBus.addListener(SourceCellItem::initColours);
            modEventBus.addListener(PortableSourceCellItem::initColours);

            AEKeyRendering.register(SourceKeyType.TYPE, SourceKey.class, SourceRenderer.INSTANCE);

            var driveCell = ArsEnergistique.makeId("block/source_drive_cell");
            ArsEngItems.getCells().forEach(cell -> StorageCellModels.registerModel(cell, driveCell));
            ArsEngItems.getPortables().forEach(portable -> StorageCellModels.registerModel(portable, driveCell));
            StorageCellModels.registerModel(ArsEngItems.CREATIVE_SOURCE_CELL, driveCell);
        }
    }
}
