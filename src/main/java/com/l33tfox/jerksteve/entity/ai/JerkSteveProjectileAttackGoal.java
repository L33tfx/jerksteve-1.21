package com.l33tfox.jerksteve.entity.ai;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.HostileEntity;

public class JerkSteveProjectileAttackGoal<T extends HostileEntity & RangedAttackMob> extends ProjectileAttackGoal {

    public JerkSteveProjectileAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
    }
}
