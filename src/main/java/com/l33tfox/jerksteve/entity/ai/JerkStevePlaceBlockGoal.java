package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class JerkStevePlaceBlockGoal extends Goal {
    private final JerkSteveEntity jerkSteve;
    private PlayerEntity target;

    public JerkStevePlaceBlockGoal(JerkSteveEntity jerkSteve) {
        this.jerkSteve = jerkSteve;
    }

    @Override
    public boolean canStart() {
        return jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public void tick() {
        Random random = jerkSteve.getRandom();
        World world = jerkSteve.getWorld();
        int i = MathHelper.floor(jerkSteve.getX() - 1.0 + random.nextDouble() * 2.0);
        int j = MathHelper.floor(jerkSteve.getY() + random.nextDouble() * 2.0);
        int k = MathHelper.floor(jerkSteve.getZ() - 1.0 + random.nextDouble() * 2.0);
        BlockPos blockPos = new BlockPos(i, j, k);
        BlockState blockState = world.getBlockState(blockPos);
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState2 = world.getBlockState(blockPos2);
        BlockState blockState3 = Blocks.DIRT.getDefaultState();
        if (blockState3 != null) {
            blockState3 = Block.postProcessState(blockState3, jerkSteve.getWorld(), blockPos);
            if (canPlaceOn(world, blockPos, blockState3, blockState, blockState2, blockPos2)) {
                world.setBlockState(blockPos, blockState3, Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(jerkSteve, blockState3));
            }
        }
    }

    private boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos) {
        return stateAbove.isAir()
                && !state.isAir()
                && !state.isOf(Blocks.BEDROCK)
                && state.isFullCube(world, pos)
                && carriedState.canPlaceAt(world, posAbove)
                && world.getOtherEntities(jerkSteve, Box.from(Vec3d.of(posAbove))).isEmpty();
    }
}
