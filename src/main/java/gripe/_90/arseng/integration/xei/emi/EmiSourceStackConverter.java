package gripe._90.arseng.integration.xei.emi;

import appeng.api.integrations.emi.EmiStackConverter;
import appeng.api.stacks.GenericStack;
import com.google.common.primitives.Ints;
import dev.emi.emi.api.stack.EmiStack;
import gripe._90.arseng.integration.xei.SourceTypeKey;
import gripe._90.arseng.me.key.SourceKey;
import org.jetbrains.annotations.Nullable;

import static gripe._90.arseng.me.key.SourceKey.KEY;

public final class EmiSourceStackConverter implements EmiStackConverter {
  @Override
  public Class<?> getKeyType() {
    return SourceTypeKey.class;
  }

  @Override
  public @Nullable EmiStack toEmiStack(GenericStack stack) {
    if (stack.what() instanceof SourceKey) {
      return new SourceEmiStack(Math.max(1, Ints.saturatedCast(stack.amount())));
    }
    return null;
  }

  @Override
  public @Nullable GenericStack toGenericStack(EmiStack stack) {
    var key = stack.getKeyOfType(SourceTypeKey.class);
    if (key != null) {
      return new GenericStack(KEY, stack.getAmount());
    }
    return null;
  }
}
