package gripe._90.arseng.ae2;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

import gripe._90.arseng.ArsEnergistique;

public class SourceKeyType extends AEKeyType {

    public static final Component SOURCE = new TranslatableComponent("gui." + ArsEnergistique.MODID + ".source");
    public static final AEKeyType TYPE = new SourceKeyType();

    public SourceKeyType() {
        super(ArsEnergistique.makeId("source"), SourceKey.class, SOURCE);
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
    public int getAmountPerOperation() {
        return 5;
    }

    @Override
    public int getAmountPerUnit() {
        return 1;
    }

    @Nullable
    @Override
    public String getUnitSymbol() {
        return null;
    }
}
