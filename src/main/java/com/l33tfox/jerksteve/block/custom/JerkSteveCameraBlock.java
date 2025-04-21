package com.l33tfox.jerksteve.block.custom;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JerkSteveCameraBlock extends FacingBlock implements Waterloggable {

    public static final MapCodec<JerkSteveCameraBlock> CODEC = createCodec(JerkSteveCameraBlock::new);
    private static final VoxelShape SHAPE1 = Block.createCuboidShape(3, 0, 5, 13, 7, 11);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(5, 0, 3, 11, 7, 13);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public JerkSteveCameraBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("itemTooltip.jerksteve.jerksteve_camera").formatted(Formatting.YELLOW));
        super.appendTooltip(stack, context, tooltip, options);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(FACING) == Direction.NORTH || state.get(FACING) == Direction.SOUTH) {
            return SHAPE1;
        }

        return SHAPE2;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            PlayerEntity closestPlayer = getClosestPlayerInSurvival(world, pos, placer, 32F);

            // spawn new JerkSteveEntity on nearest player
            if (closestPlayer != null) {
                JerkSteveEntity.spawnNew((ServerWorld) world, closestPlayer.getBlockPos(), SpawnReason.EVENT, pos);
            }
        }
    }

    // Returns the nearest player to BlockPos pos. Returns the CameraBlock placer if there are no other players nearby.
    @Nullable
    public PlayerEntity getClosestPlayerInSurvival(World world, BlockPos pos, @Nullable LivingEntity placer, double maxDistance) {
        double closestDistance = -1.0;
        PlayerEntity playerPlacer = (PlayerEntity) placer;
        PlayerEntity playerEntity = null;

        for (PlayerEntity playerEntity2 : world.getPlayers()) {
            if (!playerEntity2.isInCreativeMode() && !playerEntity2.isSpectator() && !playerEntity2.isInvisible() && !playerEntity2.equals(playerPlacer)) {
                double squaredDistance = playerEntity2.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                if ((maxDistance < 0.0 || squaredDistance < maxDistance * maxDistance) && (closestDistance == -1.0 || squaredDistance < closestDistance)) {
                    closestDistance = squaredDistance;
                    playerEntity = playerEntity2;
                }
            }
        }

        if (playerEntity == null) {
            playerEntity = playerPlacer;
        }

        if (playerEntity != null) {
            JerkSteve.LOGGER.info("returning" + playerEntity.getName());
        } else {
            JerkSteve.LOGGER.info("returning null");
        }

        return playerEntity;
    }

}
