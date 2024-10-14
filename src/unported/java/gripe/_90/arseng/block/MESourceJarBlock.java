package gripe._90.arseng.block;

import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import appeng.api.orientation.IOrientableBlock;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.hooks.WrenchHook;

import gripe._90.arseng.block.entity.MESourceJarBlockEntity;

@ParametersAreNonnullByDefault
public class MESourceJarBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, IOrientableBlock {
    private static final VoxelShape SHAPE = Stream.of(
                    Block.box(4, 13, 4, 12, 14, 12),
                    Block.box(0, 0, 0, 16, 1, 16),
                    Block.box(2, 1, 2, 14, 2, 14),
                    Block.box(3, 2, 3, 13, 13, 13),
                    Block.box(3, 14, 3, 13, 16, 13),
                    Block.box(6, 1, 14, 10, 16, 16),
                    Block.box(6, 14, 13, 10, 16, 16),
                    Block.box(5, 5, 13, 11, 11, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();

    public MESourceJarBlock() {
        super(Properties.of()
                .strength(2.2f, 11.f)
                .mapColor(MapColor.METAL)
                .sound(SoundType.GLASS)
                .noOcclusion());
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MESourceJarBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(
            Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity instanceof Player player && level.getBlockEntity(pos) instanceof MESourceJarBlockEntity jar) {
            jar.getMainNode().setOwningPlayer(player);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        var jar = (MESourceJarBlockEntity) level.getBlockEntity(pos);
        return jar != null ? jar.calculateComparatorLevel() : 0;
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : Fluids.EMPTY.defaultFluidState();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(
                        BlockStateProperties.HORIZONTAL_FACING,
                        context.getHorizontalDirection().getOpposite());
    }

    @NotNull
    @Override
    public BlockState updateShape(
            BlockState state,
            Direction dir,
            BlockState neighbourState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighbourPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, dir, neighbourState, level, pos, neighbourPos);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // Suppress break particles & sound when being disassembled by a wrench
        if (!WrenchHook.isDisassembling()) {
            super.spawnDestroyParticles(level, player, pos, state);
        }
    }
}
