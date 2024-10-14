package gripe._90.arseng.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;

import gripe._90.arseng.ArsEnergistique;
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
        // modEventBus.addListener(ArsEngClient::initBlockEntityRenderer);
    }

    private static void initCellModels(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var driveCell = ArsEnergistique.makeId("block/source_drive_cell");
            ArsEngItems.getCells().forEach(cell -> StorageCellModels.registerModel(cell, driveCell));
            ArsEngItems.getPortables().forEach(portable -> StorageCellModels.registerModel(portable, driveCell));
            StorageCellModels.registerModel(ArsEngItems.CREATIVE_SOURCE_CELL, driveCell);
        });
    }

    /*
    private static void initBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ArsEngBlockEntities.ME_SOURCE_JAR_ENTITY.get(), MESourceJarBlockEntityRenderer::new);
    }
     */

    private static void initItemColours(RegisterColorHandlersEvent.Item event) {
        ArsEngItems.getCells().forEach(cell -> event.register(SourceCellItem::getColor, cell));
        ArsEngItems.getPortables().forEach(portable -> event.register(PortableSourceCellItem::getColor, portable));
    }
}
