package gripe._90.arseng.block;

import java.util.ArrayList;
import java.util.List;
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
import appeng.api.orientation.RelativeSide;
import appeng.hooks.WrenchHook;

import gripe._90.arseng.block.entity.MESourceJarBlockEntity;

@ParametersAreNonnullByDefault
public class MESourceJarBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, IOrientableBlock {
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        var jar = (MESourceJarBlockEntity) level.getBlockEntity(pos);
        return jar != null ? jar.calculateComparatorLevel() : 0;
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var boxes = new ArrayList<VoxelShape>();
        boxes.add(Block.box(4, 13, 4, 12, 14, 12));
        boxes.add(Block.box(0, 0, 0, 16, 1, 16));
        boxes.add(Block.box(2, 1, 2, 14, 2, 14));
        boxes.add(Block.box(3, 2, 3, 13, 13, 13));
        boxes.add(Block.box(3, 14, 3, 13, 16, 13));

        var forward = getOrientation(state).getSide(RelativeSide.BACK);
        boxes.addAll(
                switch (forward) {
                    case SOUTH -> List.of(
                            Block.box(6, 1, 14, 10, 16, 16),
                            Block.box(6, 14, 13, 10, 16, 16),
                            Block.box(5, 5, 13, 11, 11, 16));
                    case NORTH -> List.of(
                            Block.box(6, 1, 0, 10, 16, 2),
                            Block.box(6, 14, 0, 10, 16, 3),
                            Block.box(5, 5, 0, 11, 11, 3));
                    case EAST -> List.of(
                            Block.box(14, 1, 6, 16, 16, 10),
                            Block.box(13, 14, 6, 16, 16, 10),
                            Block.box(13, 5, 5, 16, 11, 11));
                    case WEST -> List.of(
                            Block.box(0, 1, 6, 2, 16, 10),
                            Block.box(0, 14, 6, 3, 16, 10),
                            Block.box(0, 5, 5, 3, 11, 11));
                    default -> List.of();
                });

        return boxes.stream()
                .reduce((b1, b2) -> Shapes.join(b1, b2, BooleanOp.OR))
                .get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
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
