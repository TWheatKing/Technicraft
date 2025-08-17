package com.example.technicraft.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import com.example.technicraft.block.entity.ShaftBlockEntity;
import com.example.technicraft.registry.ModBlockEntityTypes;

/**
 * Basic shaft block for transmitting rotational energy
 * Can be oriented along different axes and connects to other kinetic components
 */
public class ShaftBlock extends BlockWithEntity {
    // Property for shaft axis orientation
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class);

    // VoxelShapes for different orientations
    private static final VoxelShape SHAPE_X = Block.createCuboidShape(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape SHAPE_Y = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final VoxelShape SHAPE_Z = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);

    public ShaftBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    /**
     * Gets the visual shape of the block based on its axis orientation
     */
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case X -> SHAPE_X;
            case Y -> SHAPE_Y;
            case Z -> SHAPE_Z;
        };
    }

    /**
     * Sets the placement state based on the face being clicked
     */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction.Axis axis = ctx.getSide().getAxis();
        return this.getDefaultState().with(AXIS, axis);
    }

    /**
     * Handles player interaction with the shaft
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShaftBlockEntity shaftEntity) {
                // Debug information when player right-clicks
                player.sendMessage(net.minecraft.text.Text.literal(
                        String.format("Shaft - Speed: %.1f RPM, Stress: %.1f/%.1f SU",
                                shaftEntity.getRotationalStorage().getSpeed(),
                                shaftEntity.getRotationalStorage().getStress(),
                                shaftEntity.getRotationalStorage().getMaxStress())
                ), false);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    /**
     * Creates the block entity for this shaft
     */
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShaftBlockEntity(pos, state);
    }

    /**
     * Gets the block entity ticker for server-side updates
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntityTypes.SHAFT, ShaftBlockEntity::tick);
    }

    /**
     * Adds properties to the block state manager
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    /**
     * Returns the render type for this block
     */
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * Checks if this shaft can connect to another kinetic block
     * @param world The world
     * @param pos This shaft's position
     * @param direction Direction to check
     * @return True if connection is possible
     */
    public boolean canConnectInDirection(World world, BlockPos pos, Direction direction) {
        BlockState state = world.getBlockState(pos);
        Direction.Axis shaftAxis = state.get(AXIS);

        // Shaft can only connect along its axis
        if (direction.getAxis() != shaftAxis) {
            return false;
        }

        BlockPos targetPos = pos.offset(direction);
        BlockState targetState = world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();

        // Check if target block is kinetic-compatible
        return targetBlock instanceof ShaftBlock ||
                targetBlock instanceof KineticGeneratorBlock ||
                targetBlock instanceof BasicMotorBlock ||
                targetBlock instanceof MechanicalCrusherBlock;
    }

    /**
     * Gets all directions this shaft can connect to
     * @param world The world
     * @param pos This shaft's position
     * @return Array of valid connection directions
     */
    public Direction[] getConnectionDirections(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Direction.Axis axis = state.get(AXIS);

        return switch (axis) {
            case X -> new Direction[]{Direction.EAST, Direction.WEST};
            case Y -> new Direction[]{Direction.UP, Direction.DOWN};
            case Z -> new Direction[]{Direction.NORTH, Direction.SOUTH};
        };
    }

    /**
     * Called when a neighboring block is updated
     */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShaftBlockEntity shaftEntity) {
                shaftEntity.onNeighborUpdate();
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
}