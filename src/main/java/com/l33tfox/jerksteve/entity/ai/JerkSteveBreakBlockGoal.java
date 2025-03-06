package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

public class JerkSteveBreakBlockGoal extends Goal {

    private final JerkSteveEntity jerkSteve;
    private static final int MIN_MAX_PROGRESS = 240;
    protected BlockPos posBelowTarget;
    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;
    private boolean blockMined;

    public JerkSteveBreakBlockGoal(JerkSteveEntity jerkSteve, int maxProgress) {
        this.jerkSteve = jerkSteve;
        blockMined = false;
    }

    protected int getMaxProgress() {
        return Math.max(40, this.maxProgress);
    }

    @Override
    public boolean canStart() {
        if (jerkSteve.getTarget() == null) {
            return false;
        }

        blockMined = false;
        boolean canSpleef = false;
        BlockPos pos2Below = JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 2); // might be able to use BlockPos.ofFloored instead

        BlockState state2Below = jerkSteve.getWorld().getBlockState(pos2Below);

        if (jerkSteve.getTarget().isOnGround() && JerkSteveUtil.isNotCollidable(state2Below)) {
            canSpleef = true;
        }

        posBelowTarget = pos2Below.add(0, 1, 0);
        return canSpleef && jerkSteve.canInteractWithBlockAt(posBelowTarget, 0) && jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public void start() {
        this.breakProgress = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.getMaxProgress() && !jerkSteve.getWorld().getBlockState(posBelowTarget).isAir()
                && !jerkSteve.getWorld().getBlockState(posBelowTarget).isOf(Blocks.BEDROCK)
                && jerkSteve.canInteractWithBlockAt(posBelowTarget, 0)
                && (posBelowTarget.equals(JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 1)) ||
                posBelowTarget.equals(JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 2)) ||
                posBelowTarget.equals(JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 3)));
    }

    @Override
    public void stop() {
        if (jerkSteve.getWorld().getBlockState(posBelowTarget).isAir() && jerkSteve.getTarget() != null && jerkSteve.getTarget().getVelocity().y <= 0 && blockMined) {
            JerkSteve.LOGGER.info("a");
            jerkSteve.successfullyAttacked = true;
        }
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

        Item tool = JerkSteveUtil.getToolToMine(jerkSteve, posBelowTarget, JerkSteveEntity.items);
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(tool));

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
            blockMined = true;
        }
    }
}
