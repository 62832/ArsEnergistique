package gripe._90.arseng.block;

import net.minecraft.world.level.material.Material;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;

import gripe._90.arseng.block.entity.SourceAcceptorBlockEntity;

public class SourceAcceptorBlock extends AEBaseEntityBlock<SourceAcceptorBlockEntity> {
    public SourceAcceptorBlock() {
        super(AEBaseBlock.defaultProps(Material.METAL));
    }
}
