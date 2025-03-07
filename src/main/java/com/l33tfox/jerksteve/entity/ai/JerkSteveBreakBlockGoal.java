package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

import java.util.EnumSet;

// based off of BreakDoorGoal
public class JerkSteveBreakBlockGoal extends Goal {

    private final JerkSteveEntity jerkSteve;
    protected BlockPos posBelowTarget;
    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;
    private boolean blockMined;

    public JerkSteveBreakBlockGoal(JerkSteveEntity jerkSteve) {
        this.jerkSteve = jerkSteve;
        blockMined = false;
        setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
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
        BlockPos pos2Below = JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 2);

        BlockState state2Below = jerkSteve.getWorld().getBlockState(pos2Below);

        if (jerkSteve.getTarget().isOnGround() && JerkSteveUtil.isNotCollidable(state2Below)) {
            canSpleef = true;
        }

        posBelowTarget = pos2Below.add(0, 1, 0);
        return canSpleef && jerkSteve.canInteractWithBlockAt(posBelowTarget, 0) && jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public void start() {
        breakProgress = 0;
    }

    @Override
    public boolean shouldContinue() {
        return jerkSteve.getTarget() != null
                && breakProgress <= getMaxProgress() && !jerkSteve.getWorld().getBlockState(posBelowTarget).isAir()
                && !jerkSteve.getWorld().getBlockState(posBelowTarget).isOf(Blocks.BEDROCK)
                && jerkSteve.canInteractWithBlockAt(posBelowTarget, 0)
                // this prevents player from spam jumping to stop JerkSteve from spleefing
                && (posBelowTarget.equals(JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 1)) ||
                posBelowTarget.equals(JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 2)) ||
                posBelowTarget.equals(JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 3)));
    }

    @Override
    public void stop() {
        // if the block was mined and the target is falling - this can probably cause some rare bugs where the player
        // is falling down from jumping as the block is mined, and still stays up on the platform, but idc enough to account for that
        if (jerkSteve.getWorld().getBlockState(posBelowTarget).isAir() && jerkSteve.getTarget() != null && jerkSteve.getTarget().getVelocity().y <= 0 && blockMined) {
            jerkSteve.successfullyAttacked = true; // allow flee target goal to start
        }
        jerkSteve.getWorld().setBlockBreakingInfo(jerkSteve.getId(), posBelowTarget, -1);
    }

    // adapted from breakdoorgoal class - pretty scuffed and hardcoded
    @Override
    public void tick() {
        LivingEntity target = jerkSteve.getTarget();

        if (target == null || posBelowTarget == null || !shouldContinue()) {
            return;
        }

        BlockPos blockToMine = posBelowTarget;

        // look at block target is standing on
        jerkSteve.getLookControl().lookAt(target.getX(), target.getBlockY() - 0.5F, target.getZ(), 30.0F, 30.0F);

        HitResult raycastResult = jerkSteve.raycast(jerkSteve.getBlockInteractionRange() + 2, 0, false);
        // check if JerkSteve can see that target block directly, or other blocks are in the way
        if (raycastResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = BlockPos.ofFloored(raycastResult.getPos());
            if (!blockPos.equals(posBelowTarget) && !jerkSteve.getWorld().getBlockState(blockPos).isAir()) {
                blockToMine = blockPos;
            }
        }

        Item tool = JerkSteveUtil.getToolToMine(jerkSteve, blockToMine, JerkSteveEntity.items);
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(tool));

        if (!jerkSteve.handSwinging) {
            jerkSteve.swingHand(jerkSteve.getActiveHand());
        }

        // this is pretty bad and hardcoded, so the break speed is the same regardless of the block. adapted from breakdoorgoal
        // maybe will change later
        this.breakProgress++;
        int m = (int) ((float) breakProgress / (float) getMaxProgress() * 30.0F);
        if (m != prevBreakProgress) {
            jerkSteve.getWorld().setBlockBreakingInfo(jerkSteve.getId(), blockToMine, m);
            prevBreakProgress = m;
        }

        // also hardcoded value
        if (m >= 7.5F) {
            jerkSteve.getWorld().breakBlock(blockToMine, true, jerkSteve);
            jerkSteve.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockToMine, Block.getRawIdFromState(jerkSteve.getWorld().getBlockState(blockToMine)));
            blockMined = true;
            breakProgress = 0;
        }
    }
}
