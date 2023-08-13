package gripe._90.arseng;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;

import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngCreativeTab;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.cell.SourceCellHandler;
import gripe._90.arseng.me.client.SourceRenderer;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@Mod(ArsEngCore.MODID)
public class ArsEnergistique {
    @SuppressWarnings("UnstableApiUsage")
    public ArsEnergistique() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ArsEngItems::register);
        bus.addListener(ArsEngCreativeTab::register);

        bus.addListener(SourceKeyType::register);
        StorageCells.addCellHandler(SourceCellHandler.INSTANCE);

        GenericSlotCapacities.register(SourceKeyType.TYPE, 10000L);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::new);
    }

    @OnlyIn(Dist.CLIENT)
    static class Client {
        Client() {
            AEKeyRendering.register(SourceKeyType.TYPE, SourceKey.class, new SourceRenderer());
            StorageCellModels.registerModel(
                    ArsEngItems.SOURCE_STORAGE_CELL, ArsEngCore.makeId("block/drive/cells/source_storage_cell"));
        }
    }
}
