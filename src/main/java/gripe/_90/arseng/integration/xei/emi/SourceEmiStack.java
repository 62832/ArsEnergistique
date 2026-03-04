package gripe._90.arseng.integration.xei.emi;

import appeng.client.gui.style.Blitter;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiStack;
import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.integration.xei.SourceStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SourceEmiStack extends EmiStack {
  private final SourceStack stack;
  static final Material SOURCE = new Material(
      InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("ars_nouveau", "block/mana_still"));

  public SourceEmiStack(int amount) {
    stack = new SourceStack(amount);
  }

  @Override
  public EmiStack copy() {
    return new SourceEmiStack(stack.getAmount());
  }

  @Override
  public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
    draw.pose().pushPose();
    Blitter.sprite(SOURCE.sprite())
        .blending(false)
        .dest(x, y, 16, 16)
        .blit(draw);
    draw.pose().popPose();
    if ((flags & RENDER_REMAINDER) != 0) {
      EmiRender.renderRemainderIcon(this, draw, x, y);
    }
  }

  public SourceStack getStack() {
    return stack;
  }

  @Override
  public boolean isEmpty() {
    return stack.isEmpty();
  }

  @Override
  public DataComponentPatch getComponentChanges() {
    return DataComponentPatch.EMPTY;
  }

  @Override
  public Object getKey() {
    return stack.getKey();
  }

  @Override
  public ResourceLocation getId() {
    return stack.getId();
  }

  @Override
  public List<Component> getTooltipText() {
    if (isEmpty()) {
      return Collections.emptyList();
    }
    List<Component> tooltips = new ArrayList<>();
    tooltips.add(getName());
    return tooltips;
  }

  @Override
  public List<ClientTooltipComponent> getTooltip() {
    List<ClientTooltipComponent> tooltips = getTooltipText().stream()
        .map(EmiTooltipComponents::of)
        .collect(Collectors.toList());
    if (amount > 1) {
      tooltips.add(EmiTooltipComponents.of(
          Component.translatable(ArsEnergistique.MODID + ".xei.source.tooltip",
              new DecimalFormat("#,###").format(amount))
              .withStyle(ChatFormatting.GRAY)));
    }

    EmiTooltipComponents.appendModName(tooltips, getId().getNamespace());
    tooltips.addAll(super.getTooltip());
    return tooltips;
  }

  @Override
  public Component getName() {
    return Component.translatable(ArsEnergistique.MODID + ".xei.source.name");
  }
}
