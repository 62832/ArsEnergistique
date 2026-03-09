package gripe._90.arseng.integration.xei.jei;

import gripe._90.arseng.integration.xei.SourceStack;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SourceIngredientHelper implements IIngredientHelper<SourceStack> {
  @Override
  public IIngredientType<SourceStack> getIngredientType() {
    return SourceIngredientConverter.SOURCE_INGREDIENT;
  }

  @Override
  public String getDisplayName(SourceStack ingredient) {
    return ingredient.getName().getString();
  }

  @Override
  @SuppressWarnings("removal")
  public String getUniqueId(SourceStack ingredient, UidContext context) {
    return ingredient.getId().toString();
  }

  @Override
  public ResourceLocation getResourceLocation(SourceStack ingredient) {
    return ingredient.getId();
  }

  @Override
  public SourceStack copyIngredient(SourceStack ingredient) {
    return ingredient.copy();
  }

  @Override
  public String getErrorInfo(@Nullable SourceStack ingredient) {
    return "";
  }
}
