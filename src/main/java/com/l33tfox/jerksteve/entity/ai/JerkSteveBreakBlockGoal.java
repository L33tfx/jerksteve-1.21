package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
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
    private static final int MIN_MAX_PROGRESS = 240;
    protected BlockPos posBelowTarget;
    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;

    public JerkSteveBreakBlockGoal(JerkSteveEntity jerkSteve, int maxProgress) {
        this.jerkSteve = jerkSteve;
    }

    protected int getMaxProgress() {
        return Math.max(40, this.maxProgress);
    }

    private int roundToBlock(double coord) {
        if (coord >= 0) {
            return (int) Math.ceil(coord);
        } else {
            return (int) Math.floor(coord);
        }
    }

    private BlockPos posXBelow(LivingEntity target, int x) {
        if (target == null) {
            return null;
        }

        return new BlockPos(roundToBlock(target.getX()), roundToBlock(target.getY() - x), roundToBlock(target.getZ()));
    }

    @Override
    public boolean canStart() {
        if (jerkSteve.getTarget() == null) {
            return false;
        }

        boolean canSpleef = false;
        BlockPos pos2Below = posXBelow(jerkSteve.getTarget(), 2);

        if (jerkSteve.getTarget().isOnGround() && jerkSteve.getWorld().getBlockState(pos2Below).isAir()) {
            canSpleef = true;
        }

        posBelowTarget = pos2Below.add(0, 1, 0);
        return canSpleef && jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public void start() {
//        Random random = jerkSteve.getRandom();
//        int i = MathHelper.floor(jerkSteve.getX() - 1.0 + random.nextDouble() * 2.0);
//        int j = MathHelper.floor(jerkSteve.getY() + random.nextDouble() * 2.0);
//        int k = MathHelper.floor(jerkSteve.getZ() - 1.0 + random.nextDouble() * 2.0);
//        blockPos = new BlockPos(i, j, k);
        this.breakProgress = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.getMaxProgress() && !jerkSteve.getWorld().getBlockState(posBelowTarget).isAir()
                && !jerkSteve.getWorld().getBlockState(posBelowTarget).isOf(Blocks.BEDROCK)
                && jerkSteve.canInteractWithBlockAt(posBelowTarget, 0)
                && (posBelowTarget.equals(posXBelow(jerkSteve.getTarget(), 1)) || posBelowTarget.equals(posXBelow(jerkSteve.getTarget(), 2)) || posBelowTarget.equals(posXBelow(jerkSteve.getTarget(), 3)));
    }

    @Override
    public void stop() {
        jerkSteve.getWorld().setBlockBreakingInfo(jerkSteve.getId(), posBelowTarget, -1);
    }

    // adapted from breakdoorgoal class - pretty scuffed and hardcoded
    @Override
    public void tick() {
        if (posBelowTarget == null || !shouldContinue()) {
            return;
        }

        jerkSteve.getLookControl().lookAt(jerkSteve.getTarget().getX(), jerkSteve.getTarget().getBlockY() - 0.5F,
                jerkSteve.getTarget().getZ(), 30.0F, 30.0F);

        if (!jerkSteve.handSwinging) {
            jerkSteve.swingHand(jerkSteve.getActiveHand());
        }

        this.breakProgress++;
        int m = (int)((float)this.breakProgress / (float)this.getMaxProgress() * 30.0F);
        if (m != this.prevBreakProgress) {
            jerkSteve.getWorld().setBlockBreakingInfo(jerkSteve.getId(), posBelowTarget, m);
            this.prevBreakProgress = m;
        }

        if (m >= 7.5F) {
            jerkSteve.getWorld().breakBlock(posBelowTarget, true, jerkSteve);
            jerkSteve.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, posBelowTarget, Block.getRawIdFromState(jerkSteve.getWorld().getBlockState(posBelowTarget)));
        }
    }
}
