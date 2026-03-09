package gripe._90.arseng.integration.xei.emi;

import com.google.common.primitives.Ints;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import gripe._90.arseng.integration.xei.SourceStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;

public class SourceEmiIngredientSerializer implements EmiStackSerializer<SourceEmiStack> {
  @Override
  public EmiStack create(ResourceLocation id, DataComponentPatch ignored, long amount) {
    return id.equals(SourceStack.EMPTY.getId()) ? new SourceEmiStack(Ints.saturatedCast(amount)) : EmiStack.EMPTY;
  }

  @Override
  public String getType() {
    return "ars_nouveau_source";
  }

  void addEmiStacks(EmiRegistry emiRegistry) {
    emiRegistry.addEmiStack(new SourceEmiStack(1));
  }
}
