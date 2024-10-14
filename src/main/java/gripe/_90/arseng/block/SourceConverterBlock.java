package gripe._90.arseng.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;

import gripe._90.arseng.block.entity.SourceConverterBlockEntity;

public class SourceConverterBlock extends AEBaseEntityBlock<SourceConverterBlockEntity> {
    public SourceConverterBlock() {
        super(AEBaseBlock.defaultProps(MapColor.METAL, SoundType.METAL));
    }
}
