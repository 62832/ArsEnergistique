package gripe._90.arseng.integration.xei;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceStack {
  protected int amount;

  public static final SourceStack EMPTY = new SourceStack(0);

  private static final SourceTypeKey KEY = new SourceTypeKey();

  public static final MapCodec<SourceStack> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
     Codec.INT.fieldOf("amount").forGetter(SourceStack::getAmount)
  ).apply(instance, SourceStack::new));

  public static final Codec<SourceStack> CODEC = MAP_CODEC.codec();

  public SourceStack(int amount) {
    this.amount = amount;
  }

  public final SourceTypeKey getKey() {
    return KEY;
  }

  public final ResourceLocation getId() {
    return KEY.getId();
  }

  public int getAmount() {
    return this.amount;
  }

  public boolean isEmpty() {
    return this == EMPTY || amount <= 0;
  }

  public Component getName() {
    return Component.literal("Source");
  }

  /**
   * @return List of components containing just the name
   */
  public List<Component> getTooltip() {
    if (isEmpty()) {
      return Collections.emptyList();
    }
    List<Component> tooltips = new ArrayList<>();
    tooltips.add(getName());
    return tooltips;
  }

  /**
   * @return List of components containing the name and the amount
   */
  public List<Component> getTooltips() {
    var tooltips = getTooltip();
    if (!isEmpty())
      tooltips.add(Component.translatable("%s Source", new DecimalFormat("#,###").format(amount)));
    return tooltips;
  }

  public SourceStack copy() {
    return new SourceStack(amount);
  }

  public SourceStack copyWithAmount(int newAmount) {
    return new SourceStack(newAmount);
  }
}
