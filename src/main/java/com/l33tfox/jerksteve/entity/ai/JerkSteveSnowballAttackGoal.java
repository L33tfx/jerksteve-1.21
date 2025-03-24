package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.util.JerkSteveUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class JerkSteveSnowballAttackGoal<T extends JerkSteveEntity> extends ProjectileAttackGoal {

    private final T jerkSteve;
    private final float range;

    // Only called in JerkSteveEntity's initGoals() when the entity is created
    public JerkSteveSnowballAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
        this.range = range;
        setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
    }

    // Starts the goal, causing it to start ticking in GoalSelector class
    @Override
    public void start() {
        super.start();
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SNOWBALL));
    }

    // Called before start() in GoalSelector class
    @Override
    public boolean canStart() {
        LivingEntity target = jerkSteve.getTarget();
        return super.canStart()
                && target != null
                && target.getHealth() >= JerkSteveUtil.BOW_ATTACK_HEALTH_THRESHOLD
                && jerkSteve.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= range * range // within range
                // middle statement below to prevent players from spamming jump in water to avoid snowball attack
                && (target.isInFluid() || jerkSteve.getWorld().getBlockState(JerkSteveUtil.posXBelow(target, 1)).isLiquid() || jerkSteve.isTargetNearDrop());
    }

    // Called after ticking the goal in GoalSelector class
    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    // Called in GoalSelector class if shouldContinue() returns false
    @Override
    public void stop() {
        LivingEntity target = jerkSteve.getTarget();
        if (target != null && !target.isOnGround() && !target.isInFluid()) { // if target is midair
            BlockPos blockPosBelowTarget = JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 1);
            BlockState blockStateBelowTarget = jerkSteve.getWorld().getBlockState(blockPosBelowTarget);
            // successfullyAttacked used in FleeTargetGoal to trigger flee
            jerkSteve.successfullyAttacked = jerkSteve.snowballLanded && JerkSteveUtil.isNotCollidable(blockStateBelowTarget);
            jerkSteve.snowballLanded = false; // reset boolean for next time he throws snowball
        }

        super.stop();
    }

}
