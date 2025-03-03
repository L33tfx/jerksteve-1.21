package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

import java.util.function.Predicate;

public class JerkSteveBreakBlockGoal extends Goal {

    private final JerkSteveEntity jerkSteve;
    private PlayerEntity target;
    private static final int MIN_MAX_PROGRESS = 240;
    protected BlockPos blockPos;
    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;

    public JerkSteveBreakBlockGoal(JerkSteveEntity jerkSteve, int maxProgress) {
        this.jerkSteve = jerkSteve;
    }

    protected int getMaxProgress() {
        return Math.max(40, this.maxProgress);
    }

    @Override
    public boolean canStart() {
        return jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public void start() {
        Random random = jerkSteve.getRandom();
        int i = MathHelper.floor(jerkSteve.getX() - 1.0 + random.nextDouble() * 2.0);
        int j = MathHelper.floor(jerkSteve.getY() + random.nextDouble() * 2.0);
        int k = MathHelper.floor(jerkSteve.getZ() - 1.0 + random.nextDouble() * 2.0);
        blockPos = new BlockPos(i, j, k);
        this.breakProgress = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.getMaxProgress() && !jerkSteve.getWorld().getBlockState(blockPos).isAir()
                && !jerkSteve.getWorld().getBlockState(blockPos).isOf(Blocks.BEDROCK);
    }

    @Override
    public void stop() {
        jerkSteve.getWorld().setBlockBreakingInfo(jerkSteve.getId(), blockPos, -1);
    }

    @Override
    public void tick() {
        super.tick();

        if (blockPos == null || !shouldContinue()) {
            return;
        }

        if (!jerkSteve.handSwinging) {
            jerkSteve.swingHand(jerkSteve.getActiveHand());
        }

        this.breakProgress++;
        int m = (int)((float)this.breakProgress / (float)this.getMaxProgress() * 30.0F);
        if (m != this.prevBreakProgress) {
            jerkSteve.getWorld().setBlockBreakingInfo(jerkSteve.getId(), blockPos, m);
            this.prevBreakProgress = m;
        }

        if (m >= 7.5F) {
            jerkSteve.getWorld().breakBlock(blockPos, true, jerkSteve);
            jerkSteve.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(jerkSteve.getWorld().getBlockState(blockPos)));
        }
    }
}
