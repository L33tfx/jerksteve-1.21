package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class JerkSteveSnowballAttackGoal<T extends JerkSteveEntity> extends ProjectileAttackGoal {

    private final T jerkSteve;
    private final float range;

    public JerkSteveSnowballAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
        this.range = range;
    }

    @Override
    public void start() {
        super.start();
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SNOWBALL));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = jerkSteve.getTarget();
        return super.canStart()
                && target != null
                && target.getHealth() >= JerkSteveUtil.bowAttackHealthThreshold
                && jerkSteve.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= range * range
                && (isTargetNearDrop() || target.isInFluid());
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    @Override
    public void stop() {
        //JerkSteve.LOGGER.info("" + jerkSteve.getTarget().getVelocity().y);
        if (jerkSteve.getTarget() != null && !jerkSteve.getTarget().isOnGround()) {
            JerkSteve.LOGGER.info("A");
            BlockPos blockPosBelowTarget = JerkSteveUtil.posXBelow(jerkSteve.getTarget(), 1);
            BlockState blockStateBelowTarget = jerkSteve.getWorld().getBlockState(blockPosBelowTarget);
            jerkSteve.successfullyAttacked = jerkSteve.snowballLanded && JerkSteveUtil.isNotCollidable(blockStateBelowTarget);
            jerkSteve.snowballLanded = false;
        }

        super.stop();
    }

    public boolean isTargetNearDrop() {
        LivingEntity target = jerkSteve.getTarget();

        if (target == null || !target.isOnGround()) {
            return false;
        }

        if (target.isOnGround() && target.isSneaking()
                && JerkSteveUtil.isNotCollidable(jerkSteve.getWorld().getBlockState(JerkSteveUtil.posXBelow(target, 1)))) {
            return true;
        }

        for (int xDisplace = -2; xDisplace <= 2; xDisplace++) {
            for (int zDisplace = -2; zDisplace <= 2; zDisplace++) {
                BlockPos blockPos = JerkSteveUtil.posXBelow(target, xDisplace, 1, zDisplace);
                if (JerkSteveUtil.isNotCollidable(jerkSteve.getWorld().getBlockState(blockPos))) {
                    BlockPos blockPos2 = blockPos.add(0, -1, 0);
                    if (JerkSteveUtil.isNotCollidable(jerkSteve.getWorld().getBlockState(blockPos2))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
