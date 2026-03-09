package gripe._90.arseng.integration.xei.jei;

import appeng.api.stacks.GenericStack;
import com.google.common.primitives.Ints;
import gripe._90.arseng.integration.xei.SourceStack;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverter;

public class SourceIngredientConverter implements IngredientConverter<SourceStack> {
  public static final IIngredientType<SourceStack> SOURCE_INGREDIENT = () -> SourceStack.class;

  @Override
  public @NotNull IIngredientType<SourceStack> getIngredientType() {
    return SOURCE_INGREDIENT;
  }

  @Override
  public @Nullable SourceStack getIngredientFromStack(GenericStack stack) {
    if (stack.what().getType().equals(SourceKeyType.TYPE)) {
      return new SourceStack(Math.max(1, Ints.saturatedCast(stack.amount())));
    }
    return null;
  }

  @Override
  public @Nullable GenericStack getStackFromIngredient(SourceStack stack) {
    if (stack.getKey().equals(SourceStack.EMPTY.getKey())) {
      return new GenericStack(SourceKey.KEY, Math.max(1L, stack.getAmount()));
    }
    return null;
  }
}
