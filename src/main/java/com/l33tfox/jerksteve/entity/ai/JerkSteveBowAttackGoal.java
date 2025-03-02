package com.l33tfox.jerksteve.entity.ai;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;

public class JerkSteveBowAttackGoal<T extends HostileEntity & RangedAttackMob> extends BowAttackGoal<T> {

    public JerkSteveBowAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
    }
}
