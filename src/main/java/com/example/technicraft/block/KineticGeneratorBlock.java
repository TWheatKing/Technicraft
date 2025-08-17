package com.example.technicraft.block;

import com.example.technicraft.block.entity.KineticGeneratorBlockEntity;
import com.example.technicraft.registry.ModBlockEntityTypes;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Hand crank kinetic generator block
 * Players can interact with it to generate rotational energy
 */
public class KineticGeneratorBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    // Shape of the generator block
    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public KineticGeneratorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    /**
     * Gets the visual shape of the block
     */
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    /**
     * Sets the placement state based on player orientation
     */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    /**
     * Handles player interaction - generates rotational energy when used
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KineticGeneratorBlockEntity generator) {
                // Generate rotational energy when cranked
                boolean success = generator.crank(player);
                if (success) {
                    // Play cranking sound and particles could be added here
                    player.sendMessage(net.minecraft.text.Text.literal(
                            String.format("Cranked! Speed: %.1f RPM",
                                    generator.getRotationalStorage().getSpeed())
                    ), true);
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(net.minecraft.text.Text.literal("Generator is overstressed!"), true);
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }

    /**
     * Creates the block entity for this generator
     */
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KineticGeneratorBlockEntity(pos, state);
    }

    /**
     * Gets the block entity ticker for server-side updates
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntityTypes.KINETIC_GENERATOR, KineticGeneratorBlockEntity::tick);
    }

    /**
     * Adds properties to the block state manager
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * Returns the render type for this block
     */
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * Handle block rotation
     */
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    /**
     * Handle block mirroring
     */
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    /**
     * Gets the direction this generator outputs kinetic energy
     * @param state Block state
     * @return Output direction
     */
    public Direction getOutputDirection(BlockState state) {
        return Direction.UP; // Generators output upward by default
    }

    /**
     * Called when a neighboring block is updated
     */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KineticGeneratorBlockEntity generator) {
                generator.onNeighborUpdate();
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }