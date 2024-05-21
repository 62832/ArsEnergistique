package gripe._90.arseng.mixin.aecapfix;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraftforge.common.util.LazyOptional;

import gripe._90.aecapfix.AECapFix;
import gripe._90.arseng.block.entity.IAdvancedSourceTile;
import gripe._90.arseng.part.SourceAcceptorPart;

@Mixin(SourceAcceptorPart.class)
public abstract class SourceAcceptorPartMixin implements AECapFix.Invalidator {
    @Shadow(remap = false)
    @Final
    private LazyOptional<IAdvancedSourceTile> adaptorHolder;

    @Override
    public void aecapfix$invalidate() {
        adaptorHolder.invalidate();
    }
}
