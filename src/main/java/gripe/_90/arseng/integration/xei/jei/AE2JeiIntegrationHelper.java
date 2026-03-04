package gripe._90.arseng.integration.xei.jei;

import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverters;

public class AE2JeiIntegrationHelper {
  public static void register() {
    IngredientConverters.register(new SourceIngredientConverter());
  }
}
