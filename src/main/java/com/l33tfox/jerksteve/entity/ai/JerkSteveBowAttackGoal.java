package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class JerkSteveBowAttackGoal<T extends HostileEntity & RangedAttackMob> extends BowAttackGoal<T> {

    private final T jerkSteve;

    public JerkSteveBowAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
    }

    @Override
    public void start() {
        super.start();
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public boolean canStart() {
        return jerkSteve.getTarget() != null && jerkSteve.getTarget().getHealth() < JerkSteveUtil.bowAttackHealthThreshold;
    }

    @Override
    public boolean shouldContinue() {
        return canStart() && isHoldingBow();
    }
}
