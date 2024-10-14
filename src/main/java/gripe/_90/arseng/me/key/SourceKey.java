package gripe._90.arseng.me.key;

import java.util.List;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

public class SourceKey extends AEKey {
    public static final SourceKey KEY = new SourceKey();
    static final MapCodec<SourceKey> MAP_CODEC = MapCodec.unit(KEY);
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("ars_nouveau", "source");

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
    public CompoundTag toTag(HolderLookup.Provider registries) {
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
    public void writeToPacket(RegistryFriendlyByteBuf data) {}

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        // Source is voided
    }

    @Override
    public boolean hasComponents() {
        return false;
    }
}
