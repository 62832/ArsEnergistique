package gripe._90.arseng.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngBlockEntities;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.item.PortableSourceCellItem;
import gripe._90.arseng.item.SourceCellItem;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@SuppressWarnings("unused")
@Mod(value = ArsEnergistique.MODID, dist = Dist.CLIENT)
public class ArsEngClient {
    public ArsEngClient(IEventBus modEventBus) {
        AEKeyRendering.register(SourceKeyType.TYPE, SourceKey.class, SourceRenderer.INSTANCE);
        modEventBus.addListener(ArsEngClient::initItemColours);
        modEventBus.addListener(ArsEngClient::initCellModels);
        modEventBus.addListener(ArsEngClient::initBlockEntityRenderer);
    }

    private static void initCellModels(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var prefix = "block/drive/cells/";
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_CELL_1K, ArsEnergistique.makeId(prefix + "1k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.PORTABLE_SOURCE_CELL1K, ArsEnergistique.makeId(prefix + "1k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_CELL_4K, ArsEnergistique.makeId(prefix + "4k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.PORTABLE_SOURCE_CELL4K, ArsEnergistique.makeId(prefix + "4k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_CELL_16K, ArsEnergistique.makeId(prefix + "16k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.PORTABLE_SOURCE_CELL16K, ArsEnergistique.makeId(prefix + "16k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_CELL_64K, ArsEnergistique.makeId(prefix + "64k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.PORTABLE_SOURCE_CELL64K, ArsEnergistique.makeId(prefix + "64k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_CELL_256K, ArsEnergistique.makeId(prefix + "256k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.PORTABLE_SOURCE_CELL256K, ArsEnergistique.makeId(prefix + "256k_source_cell"));
            StorageCellModels.registerModel(
                    ArsEngItems.CREATIVE_SOURCE_CELL, ArsEnergistique.makeId(prefix + "creative_source_cell"));
        });
    }

    private static void initBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ArsEngBlockEntities.ME_SOURCE_JAR.get(), MESourceJarBlockEntityRenderer::new);
    }

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        ArsEngItems.getCells().forEach(cell -> event.register(SourceCellItem::getColor, cell));
        ArsEngItems.getPortables().forEach(portable -> event.register(PortableSourceCellItem::getColor, portable));
    }
}
