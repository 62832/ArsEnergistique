package gripe._90.arseng;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;
import gripe._90.arseng.me.cell.SourceCellHandler;
import gripe._90.arseng.me.client.SourceRenderer;
import gripe._90.arseng.me.key.SourceKeyType;

@Mod(ArsEngCore.MODID)
public class ArsEnergistique {
    public ArsEnergistique() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ArsEngItems::register);

        bus.addListener(SourceKeyType::register);
        bus.addListener(SourceCellHandler::register);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::new);
    }

    @OnlyIn(Dist.CLIENT)
    static class Client {
        Client() {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();

            bus.addListener(SourceRenderer::register);
            bus.addListener(SourceCellHandler::registerClient);
        }
    }
}
