package gripe._90.arseng;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.storage.StorageCells;
import appeng.parts.automation.StackWorldBehaviors;

import gripe._90.arseng.block.entity.SourceConverterBlockEntity;
import gripe._90.arseng.definition.ArsEngBlockEntities;
import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngComponents;
import gripe._90.arseng.definition.ArsEngConfig;
import gripe._90.arseng.definition.ArsEngCreativeTab;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.cell.CreativeSourceCellHandler;
import gripe._90.arseng.me.cell.SourceCellHandler;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;
import gripe._90.arseng.me.misc.GenericStackSourceStorage;
import gripe._90.arseng.me.strategy.SourceContainerItemStrategy;
import gripe._90.arseng.me.strategy.SourceExternalStorageStrategy;
import gripe._90.arseng.me.strategy.SourceStorageExportStrategy;
import gripe._90.arseng.me.strategy.SourceStorageImportStrategy;
import gripe._90.arseng.part.SourceConverterPart;
import gripe._90.arseng.part.SpellP2PTunnelPart;

@Mod(ArsEnergistique.MODID)
public class ArsEnergistique {
    public static final String MODID = "arseng";

    public static ResourceLocation makeId(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    @SuppressWarnings("UnstableApiUsage")
    public ArsEnergistique(ModContainer container, IEventBus modEventBus) {
        container.registerConfig(ModConfig.Type.COMMON, ArsEngConfig.SPEC);

        ArsEngItems.DR.register(modEventBus);
        ArsEngBlockEntities.DR.register(modEventBus);
        ArsEngBlocks.DR.register(modEventBus);
        ArsEngComponents.DR.register(modEventBus);
        ArsEngCreativeTab.DR.register(modEventBus);

        modEventBus.addListener(SourceKeyType::register);
        StorageCells.addCellHandler(SourceCellHandler.INSTANCE);
        StorageCells.addCellHandler(CreativeSourceCellHandler.INSTANCE);
        modEventBus.addListener(ArsEngItems::initCellUpgrades);

        StackWorldBehaviors.registerImportStrategy(SourceKeyType.TYPE, SourceStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SourceKeyType.TYPE, SourceStorageExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(SourceKeyType.TYPE, SourceExternalStorageStrategy::new);

        ContainerItemStrategies.register(SourceKeyType.TYPE, SourceKey.class, SourceContainerItemStrategy.INSTANCE);
        GenericSlotCapacities.register(SourceKeyType.TYPE, (long) SourceKeyType.MAX_SOURCE);

        modEventBus.addListener(GenericStackSourceStorage::registerCapability);
        modEventBus.addListener(SourceConverterBlockEntity::registerCapability);
        modEventBus.addListener(SourceConverterPart::registerCapability);

        modEventBus.addListener(ArsEngItems::initP2PAttunement);
        NeoForge.EVENT_BUS.addListener(SpellP2PTunnelPart::onSpellHit);
    }
}
