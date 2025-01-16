package gripe._90.arseng.client;

import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.definition.ArsEngBlockEntities;
import gripe._90.arseng.definition.ArsEngItems;
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

    private static void initCellModels(FMLCommonSetupEvent event) {
        // Has to be done in common setup, otherwise textures are broken when first entering a world until one forces a
        // resource pack reload.
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
        });
    }

    private static void initBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ArsEngBlockEntities.ME_SOURCE_JAR.get(), MESourceJarBlockEntityRenderer::new);
    }

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        for (var cell : ArsEngItems.getCells()) {
            event.register(
                    (stack, tintIndex) -> FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex)), cell);
        }

        for (var portable : ArsEngItems.getPortables()) {
            event.register(
                    (stack, tintIndex) -> FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex)),
                    portable);
        }
    }
}
