package gripe._90.arseng;

import gripe._90.arseng.definition.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.parts.automation.StackWorldBehaviors;

import gripe._90.arseng.me.cell.SourceCellHandler;
import gripe._90.arseng.me.client.SourceRenderer;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;
import gripe._90.arseng.me.strategy.SourceContainerItemStrategy;
import gripe._90.arseng.me.strategy.SourceExternalStorageStrategy;
import gripe._90.arseng.me.strategy.SourceStorageExportStrategy;
import gripe._90.arseng.me.strategy.SourceStorageImportStrategy;
import gripe._90.arseng.part.SourceP2PTunnelPart;

@Mod(ArsEngCore.MODID)
public class ArsEnergistique {
    @SuppressWarnings("UnstableApiUsage")
    public ArsEnergistique() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ArsEngBlocks.DoSetup(bus);

        bus.addListener(ArsEngItems::register);
        bus.addListener(ArsEngCreativeTab::register);

        bus.addListener(SourceKeyType::register);
        StorageCells.addCellHandler(SourceCellHandler.INSTANCE);

        bus.addListener(ArsEngCapabilities::register);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, ArsEngCapabilities::attach);

        StackWorldBehaviors.registerImportStrategy(SourceKeyType.TYPE, SourceStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SourceKeyType.TYPE, SourceStorageExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(SourceKeyType.TYPE, SourceExternalStorageStrategy::new);

        ContainerItemStrategies.register(SourceKeyType.TYPE, SourceKey.class, new SourceContainerItemStrategy());
        GenericSlotCapacities.register(SourceKeyType.TYPE, (long) SourceContainerItemStrategy.MAX_SOURCE);

        bus.addListener(SourceP2PTunnelPart::initAttunement);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::new);
    }

    @OnlyIn(Dist.CLIENT)
    static class Client {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(SourceCellHandler::initLED);

            AEKeyRendering.register(SourceKeyType.TYPE, SourceKey.class, new SourceRenderer());
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_STORAGE_CELL, ArsEngCore.makeId("block/drive/cells/source_storage_cell"));
        }
    }
}
