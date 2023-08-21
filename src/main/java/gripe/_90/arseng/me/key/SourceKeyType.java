package gripe._90.arseng.me.key;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.RegisterEvent;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;

import gripe._90.arseng.definition.ArsEngCore;

public class SourceKeyType extends AEKeyType {
    public static final Component SOURCE = Component.translatable("ars_nouveau.category.source");
    public static final AEKeyType TYPE = new SourceKeyType();

    public SourceKeyType() {
        super(ArsEngCore.makeId("source"), SourceKey.class, SOURCE);
    }

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            AEKeyTypes.register(TYPE);
        }
    }

    @Nullable
    @Override
    public AEKey readFromPacket(FriendlyByteBuf friendlyByteBuf) {
        return SourceKey.KEY;
    }

    @Nullable
    @Override
    public AEKey loadKeyFromTag(CompoundTag compoundTag) {
        return SourceKey.KEY;
    }

    @Override
    public int getAmountPerByte() {
        return 1000;
    }

    @Override
    public int getAmountPerOperation() {
        return 1000;
    }
}
