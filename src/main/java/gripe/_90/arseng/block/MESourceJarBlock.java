package gripe._90.arseng.block;

import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.hooks.WrenchHook;

import gripe._90.arseng.block.entity.MESourceJarBlockEntity;
import gripe._90.arseng.me.key.SourceKey;

@ParametersAreNonnullByDefault
public class MESourceJarBlock extends AEBaseEntityBlock<MESourceJarBlockEntity> implements SimpleWaterloggedBlock {
    private static final VoxelShape SHAPE = Stream.of(
                    Block.box(4, 13, 4, 12, 14, 12),
                    Block.box(0, 0, 0, 16, 1, 16),
                    Block.box(2, 1, 2, 14, 2, 14),
                    Block.box(3, 2, 3, 13, 13, 13),
                    Block.box(3, 14, 3, 13, 16, 13))
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
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MESourceJarBlockEntity jar
                ? (int) Math.floor((double) jar.clampedFill() / SourceKey.MAX_SOURCE * 15)
                : 0;
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
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
