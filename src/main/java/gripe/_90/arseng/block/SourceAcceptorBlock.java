package gripe._90.arseng.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SourceAcceptorBlock extends Block implements EntityBlock {

    public SourceAcceptorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SourceAcceptorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState p_49849_, @Nullable LivingEntity living, ItemStack p_49851_) {
        var blockEntity = level.getBlockEntity(pos);

        if(blockEntity instanceof SourceAcceptorBlockEntity sourceAcceptor && living instanceof Player player){
            sourceAcceptor.setOwner(player);
        }
    }
}
