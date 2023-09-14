package gripe._90.arseng.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;

import gripe._90.arseng.block.entity.SourceAcceptorBlockEntity;

public class SourceAcceptorBlock extends AEBaseEntityBlock<SourceAcceptorBlockEntity> {
    public SourceAcceptorBlock() {
        super(AEBaseBlock.defaultProps(MapColor.METAL, SoundType.METAL));
    }
}
