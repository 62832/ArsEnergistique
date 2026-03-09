package gripe._90.arseng.integration.xei.emi;

import appeng.api.integrations.emi.EmiStackConverters;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class ArsEngEmiPlugin implements EmiPlugin {
  private static final SourceEmiIngredientSerializer SOURCE_SERIALIZER = new SourceEmiIngredientSerializer();

  @Override
  public void initialize(EmiInitRegistry registry) {
    registry.addIngredientSerializer(SourceEmiStack.class, SOURCE_SERIALIZER);
  }

  @Override
  public void register(EmiRegistry registry) {
    SOURCE_SERIALIZER.addEmiStacks(registry);
    EmiStackConverters.register(new EmiSourceStackConverter());
  }
}
