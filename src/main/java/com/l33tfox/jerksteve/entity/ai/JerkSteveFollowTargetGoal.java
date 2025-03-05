package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.mixin.WanderNearTargetGoalAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.Vec3d;

public class JerkSteveFollowTargetGoal extends WanderNearTargetGoal {

    private final JerkSteveEntity jerkSteve;
    private Path path;

    public JerkSteveFollowTargetGoal(JerkSteveEntity jerkSteve, double speed, float maxDistance) {
        super(jerkSteve, speed, maxDistance);
        this.jerkSteve = jerkSteve;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = jerkSteve.getTarget();
        ((WanderNearTargetGoalAccessor) this).setTarget(target);

        JerkSteve.LOGGER.info("c");
        if (target != null && target.isAlive()) { //&& !jerkSteve.canAttackGoalStart()) {
            Vec3d vec3d = NoPenaltyTargeting.findTo(jerkSteve, 100, 100, target.getPos(), (float) (Math.PI / 2));
            JerkSteve.LOGGER.info("a");
            if (vec3d != null) {
                JerkSteve.LOGGER.info("b");
                ((WanderNearTargetGoalAccessor) this).setTargetX(vec3d.x);
                ((WanderNearTargetGoalAccessor) this).setTargetY(vec3d.y);
                ((WanderNearTargetGoalAccessor) this).setTargetZ(vec3d.z);
                return true;
            }
        }

        return false;
    }

//    @Override
//    public void start() {
//        path = jerkSteve.getNavigation().findPathTo(target, 2);
//        jerkSteve.getNavigation().startMovingAlong(this.path, 1.0);
//    }

    @Override
    public boolean shouldContinue() {
        return !jerkSteve.getNavigation().isIdle() && canStart();
    }

//    @Override
//    public void stop() {
//        jerkSteve.getNavigation().stop();
//    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }
}
