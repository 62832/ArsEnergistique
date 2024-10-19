package gripe._90.arseng.me.key;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.RegisterEvent;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;

import gripe._90.arseng.ArsEnergistique;

public class SourceKeyType extends AEKeyType {
    public static final Component SOURCE = Component.translatable("ars_nouveau.category.source");
    public static final AEKeyType TYPE = new SourceKeyType();
    private static final MapCodec<SourceKey> MAP_CODEC = MapCodec.unit(SourceKey.KEY);

    private SourceKeyType() {
        super(ArsEnergistique.makeId("source"), SourceKey.class, SOURCE);
    }

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            AEKeyTypes.register(TYPE);
        }
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return MAP_CODEC;
    }

    @Nullable
    @Override
    public AEKey readFromPacket(RegistryFriendlyByteBuf friendlyByteBuf) {
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
