package gripe._90.arseng.me.key;

import java.util.List;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (var i = 0; i < Math.min((amount + 999) / 1000, 10); i++) {
            var data = GlowParticleData.createData(
                    ParticleColor.defaultParticleColor(), (float) Math.random() / 3F, 0.8F, 2);
            serverLevel.sendParticles(
                    data,
                    pos.getX() + 0.3 + Math.random() * 0.5,
                    pos.getY() + 0.6 + Math.random() * 0.25,
                    pos.getZ() + Math.random(),
                    8,
                    0.1,
                    0.1,
                    0.1,
                    0.04);
        }
    }
}
