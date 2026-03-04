package gripe._90.arseng.integration.xei.jei;

import appeng.client.gui.style.Blitter;
import gripe._90.arseng.integration.xei.SourceStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SourceIngredientRenderer implements IIngredientRenderer<SourceStack> {
  static final Material SOURCE = new Material(
      InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("ars_nouveau", "block/mana_still"));
  @Override
  public void render(GuiGraphics guiGraphics, SourceStack ingredient) {
    guiGraphics.pose().pushPose();
    Blitter.sprite(SOURCE.sprite())
            .blending(false)
            .dest(0, 0, 16, 16)
            .blit(guiGraphics);
    guiGraphics.pose().popPose();
  }

  @Override
  public List<Component> getTooltip(SourceStack ingredient, TooltipFlag tooltipFlag) {
    return ingredient.getTooltip();
  }
}
