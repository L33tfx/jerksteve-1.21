package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
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
    private BlockState blockState3 = Blocks.DIRT.getDefaultState();

    public JerkStevePlaceBlockGoal(JerkSteveEntity jerkSteve) {
        this.jerkSteve = jerkSteve;
    }

    @Override
    public boolean canStart() {
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(blockState3.getBlock()));
        return jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    // adapted from enderman's placeblockgoal
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
        BlockSoundGroup blockSoundGroup = blockState3.getSoundGroup();
        if (blockState3 != null) {
            blockState3 = Block.postProcessState(blockState3, jerkSteve.getWorld(), blockPos);
            if (canPlaceOn(world, blockPos, blockState3, blockState, blockState2, blockPos2)) {
                world.setBlockState(blockPos, blockState3, Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(jerkSteve, blockState3));
                world.playSound(
                        jerkSteve,
                        blockPos,
                        blockState3.getSoundGroup().getPlaceSound(),
                        SoundCategory.BLOCKS,
                        (blockSoundGroup.getVolume() + 1.0F) / 2.0F,
                        blockSoundGroup.getPitch() * 0.8F
                );
            }
        }
    }

    private boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos) {
        return stateAbove.isAir()
                && !state.isAir()
                && !state.isOf(Blocks.BEDROCK)
                && state.isFullCube(world, pos)
                && carriedState.canPlaceAt(world, posAbove)
                && world.getOtherEntities(null, Box.from(Vec3d.of(posAbove))).isEmpty();
    }
}
