package com.l33tfox.jerksteve.entity.ai;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class JerkSteveEggAttackGoal<T extends HostileEntity & RangedAttackMob> extends ProjectileAttackGoal {

    private final T jerkSteve;

    public JerkSteveEggAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
    }

    @Override
    public boolean canStart() {
        //jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.canStart();
    }

}
