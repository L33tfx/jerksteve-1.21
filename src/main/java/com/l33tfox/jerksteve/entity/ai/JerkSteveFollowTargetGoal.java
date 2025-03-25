package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.EnumSet;

// class based off of MeleeAttackGoal
public class JerkSteveFollowTargetGoal extends Goal {
    protected final JerkSteveEntity jerkSteve;
    private final double speed;
    private final boolean pauseWhenSteveIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private long lastUpdateTime;

    public JerkSteveFollowTargetGoal(JerkSteveEntity jerkSteve, double speed, boolean pauseWhenSteveIdle) {
        this. jerkSteve = jerkSteve;
        this.speed = speed;
        this.pauseWhenSteveIdle = pauseWhenSteveIdle;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    // mostly copied from MeleeAttackGoal
    @Override
    public boolean canStart() {
        LivingEntity target = jerkSteve.getTarget();
        long time = jerkSteve.getWorld().getTime();

        if (time - lastUpdateTime < 5L) {
            return false;
        }

        lastUpdateTime = time;

        if (target == null || !target.isAlive()) {
            return false;
        }

        path = jerkSteve.getNavigation().findPathTo(target, 0);

        if (jerkSteve.squaredDistanceTo(target) > 1600.0F) {
            tryTeleport();
        }

        // only start() and try to move closer to target if path can be found and more than 3 blocks away from target
        return path != null && jerkSteve.squaredDistanceTo(target) > 9.0F;
    }

    public void tryTeleport() {
        jerkSteve.tryTeleportNearTarget();
        jerkSteve.onLanding();
        jerkSteve.damage(jerkSteve.getDamageSources().fall(), 0.5F);
        jerkSteve.getWorld().playSound(null, jerkSteve.getX(), jerkSteve.getY(), jerkSteve.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = jerkSteve.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (!pauseWhenSteveIdle) { // this is always false for JerkSteveEntities - i just included it since its in MeleeAttackGoal's shouldContinue()
            return !jerkSteve.getNavigation().isIdle();
        }

        if (path == null || jerkSteve.squaredDistanceTo(target) > 1600.0F) {
            tryTeleport();
        }

        return jerkSteve.squaredDistanceTo(target) > 9.0F && !target.isSpectator() && !((PlayerEntity) target).isCreative();
    }

    @Override
    public void start() {
        jerkSteve.getNavigation().startMovingAlong(path, speed);
        jerkSteve.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        updateCountdownTicks = 0;
    }

    @Override
    public void stop() {
        LivingEntity target = jerkSteve.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(target)) {
            jerkSteve.setTarget(null);
        }

        jerkSteve.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    // mostly copied from MeleeAttackGoal again
    @Override
    public void tick() {
        LivingEntity target = jerkSteve.getTarget();
        if (target != null) {
            updateCountdownTicks = Math.max(updateCountdownTicks - 1, 0);
            if ((pauseWhenSteveIdle || jerkSteve.getVisibilityCache().canSee(target))
                    && updateCountdownTicks <= 0
                    && (targetX == 0.0 && targetY == 0.0 && targetZ == 0.0
                            || target.squaredDistanceTo(targetX, targetY, targetZ) >= 1.0
                            || jerkSteve.getRandom().nextFloat() < 0.05F)) {
                targetX = target.getX();
                targetY = target.getY();
                targetZ = target.getZ();
                updateCountdownTicks = 4 + jerkSteve.getRandom().nextInt(7);
                double d = jerkSteve.squaredDistanceTo(target);

                if (!jerkSteve.getNavigation().startMovingTo(target, speed)) {
                    updateCountdownTicks += 5;
                }

                updateCountdownTicks = getTickCount(updateCountdownTicks);
            }

        }
    }
}
