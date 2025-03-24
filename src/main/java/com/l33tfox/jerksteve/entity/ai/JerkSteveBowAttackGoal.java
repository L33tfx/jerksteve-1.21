package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.util.JerkSteveUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.EnumSet;

// Goal to shoot target and go for the kill when target's health is low
public class JerkSteveBowAttackGoal<T extends JerkSteveEntity> extends BowAttackGoal<T> {

    private final JerkSteveEntity jerkSteve;
    private final float range;

    public JerkSteveBowAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
        this.range = range;
        setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
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
                && target.getHealth() < JerkSteveUtil.BOW_ATTACK_HEALTH_THRESHOLD
                && jerkSteve.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= range * range;
    }

    @Override
    public void stop() {
        // if target died, attack was success and flee target goal should start
        if (jerkSteve.getTarget() == null || !jerkSteve.getTarget().isAlive()) {
            jerkSteve.successfullyAttacked = true;
        }

        super.stop();
    }

    @Override
    public boolean shouldContinue() {
        return canStart() && isHoldingBow();
    }
}
