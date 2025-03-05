package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class JerkSteveSnowballAttackGoal<T extends HostileEntity & RangedAttackMob> extends ProjectileAttackGoal {

    private final T jerkSteve;

    public JerkSteveSnowballAttackGoal(T actor, double speed, int attackInterval, float range) {
        super(actor, speed, attackInterval, range);
        jerkSteve = actor;
    }

    @Override
    public void start() {
        super.start();
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SNOWBALL));
    }

    @Override
    public boolean canStart() {
        return super.canStart()
                && jerkSteve.getTarget() != null
                && jerkSteve.getTarget().getHealth() >= JerkSteveUtil.bowAttackHealthThreshold
                && isTargetNearDrop();
    }

    public boolean isTargetNearDrop() {
        LivingEntity target = jerkSteve.getTarget();

        if (target == null || !target.isOnGround()) {
            return false;
        }

        for (int xDisplace = -2; xDisplace <= 2; xDisplace++) {
            for (int zDisplace = -2; zDisplace <= 2; zDisplace++) {
                BlockPos blockPos = JerkSteveUtil.posXBelow(target, xDisplace, 1, zDisplace);
                if (JerkSteveUtil.isNotCollidable(jerkSteve.getWorld().getBlockState(blockPos))) {
                    BlockPos blockPos2 = JerkSteveUtil.posXBelow(target, xDisplace, 2, zDisplace);
                    if (JerkSteveUtil.isNotCollidable(jerkSteve.getWorld().getBlockState(blockPos2))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
