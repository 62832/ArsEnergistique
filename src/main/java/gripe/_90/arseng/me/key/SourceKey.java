package gripe._90.arseng.me.key;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

public class SourceKey extends AEKey {
    public static final AEKey KEY = new SourceKey();

    private static final ResourceLocation ID = new ResourceLocation("ars_nouveau", "source");

    private SourceKey() {}

    @Override
    public AEKeyType getType() {
        return SourceKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        return new CompoundTag();
    }

    @Override
    public Object getPrimaryKey() {
        return this;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Component computeDisplayName() {
        return SourceKeyType.SOURCE;
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {}

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        // Source is voided
    }
}
