package net.jwn.jwnnpc.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MyNPC extends HorizontalDirectionalBlock {
    public static final MapCodec<MyNPC> CODEC = simpleCodec(MyNPC::new);

    public static final EnumProperty<Direction> FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    private static final Map<Direction, VoxelShape> SHAPES_LOWER;
    private static final Map<Direction, VoxelShape> SHAPES_UPPER;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        SHAPES_LOWER = new HashMap<>() {{
            put(Direction.EAST, Shapes.or(
                    Block.box(6.0, 0.0, 4.0, 10.0, 12.0, 12.0),
                    Block.box(6.0, 12.0, 0.0, 10.0, 16.0, 16.0)));
            put(Direction.WEST, Shapes.or(
                    Block.box(6.0, 0.0, 4.0, 10.0, 12.0, 12.0),
                    Block.box(6.0, 12.0, 0.0, 10.0, 16.0, 16.0)));
            put(Direction.SOUTH, Shapes.or(
                    Block.box(4.0, 0.0, 6.0, 12.0, 12.0, 10.0),
                    Block.box(0.0, 12.0, 6.0, 16.0, 16.0, 10.0)));
            put(Direction.NORTH, Shapes.or(
                    Block.box(4.0, 0.0, 6.0, 12.0, 12.0, 10.0),
                    Block.box(0.0, 12.0, 6.0, 16.0, 16.0, 10.0)));
        }};
        SHAPES_UPPER = new HashMap<>() {{
            put(Direction.EAST, Shapes.or(
                    Block.box(6.0, 0.0, 0.0, 10.0, 8, 16.0),
                    Block.box(4.0, 8.0, 4.0, 12.0, 16, 12.0)));
            put(Direction.WEST, Shapes.or(
                    Block.box(6.0, 0.0, 4.0, 10.0, 8, 16.0),
                    Block.box(4.0, 8.0, 4.0, 12.0, 16, 12.0)));
            put(Direction.SOUTH, Shapes.or(
                    Block.box(0.0, 0.0, 6.0, 16.0, 8, 10.0),
                    Block.box(4.0, 8.0, 4.0, 12.0, 16, 12.0)));
            put(Direction.NORTH, Shapes.or(
                    Block.box(0.0, 0.0, 6.0, 16.0, 8, 10.0),
                    Block.box(4.0, 8.0, 4.0, 12.0, 16, 12.0)));
        }};
    }

    public MyNPC(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            player.displayClientMessage(Component.literal("Hi!"), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER
                ? SHAPES_UPPER.get(state.getValue(FACING))
                : SHAPES_LOWER.get(state.getValue(FACING));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxY() && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(level, blockpos, Direction.UP) : blockstate.is(this);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean isLower = half == DoubleBlockHalf.LOWER;

        if (direction == (isLower ? Direction.UP : Direction.DOWN)) {
            if (neighborState.getBlock() instanceof MyNPC && neighborState.getValue(HALF) != half) {
                return neighborState.setValue(HALF, half);
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (isLower && direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
