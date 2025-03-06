package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class JerkSteveBowAttackGoal<T extends JerkSteveEntity> extends BowAttackGoal<T> {

    private final JerkSteveEntity jerkSteve;
    private final float range;

    public JerkSteveBowAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
        this.range = range;
    }

    @Override
    public void start() {
        super.start();
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = jerkSteve.getTarget();

        return target != null
                && target.getHealth() < JerkSteveUtil.bowAttackHealthThreshold
                && jerkSteve.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= range * range;
    }

    @Override
    public void stop() {
        if (jerkSteve.getTarget() != null && !jerkSteve.getTarget().isAlive()) {
            jerkSteve.successfullyAttacked = jerkSteve.projectileThrown;
            jerkSteve.projectileThrown = false;
        }

        super.stop();
    }

    @Override
    public boolean shouldContinue() {
        return canStart() && isHoldingBow();
    }
}
