package gripe._90.arseng.integration.xei.jei;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.integration.xei.SourceStack;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class ArsEngJeiPlugin implements IModPlugin {

  public ArsEngJeiPlugin() {
    if (isModLoaded("ae2jeiintegration")) {
      AE2JeiIntegrationHelper.register();
    }
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    if (isModLoaded("ae2jeiintegration")) {
      registration.register(SourceIngredientConverter.SOURCE_INGREDIENT, List.of(new SourceStack(1)),
          new SourceIngredientHelper(), new SourceIngredientRenderer(), SourceStack.CODEC);
    }
  }

  public ResourceLocation getPluginUid() {
    return ArsEnergistique.makeId("jei_plugin");
  }

  private static boolean isModLoaded(String modId) {
    if (ModList.get() == null) {
      var list = LoadingModList.get().getMods().stream().map(ModInfo::getModId);
      Objects.requireNonNull(modId);
      return list.anyMatch(modId::equals);
    } else {
      return ModList.get().isLoaded(modId);
    }
  }
}
