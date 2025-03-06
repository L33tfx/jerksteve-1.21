package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class JerkSteveFleeTargetGoal<T extends LivingEntity> extends FleeEntityGoal<T> {

    private final JerkSteveEntity jerkSteve;

    public JerkSteveFleeTargetGoal(JerkSteveEntity mob, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(mob, fleeFromType, distance, slowSpeed, fastSpeed);

        jerkSteve = mob;
    }

    @Override
    public boolean canStart() {
        targetEntity = (T) jerkSteve.getTarget();

        if (this.targetEntity == null || !jerkSteve.successfullyAttacked) {
            return false;
        } else {
            JerkSteve.LOGGER.info("b");

            Vec3d vec3d = NoPenaltyTargeting.findFrom(jerkSteve, 30, 15, this.targetEntity.getPos());
            JerkSteve.LOGGER.info("steve pos: " + jerkSteve.getPos());
            JerkSteve.LOGGER.info("target pos: " + targetEntity.getPos());
            if (vec3d == null) {
                JerkSteve.LOGGER.info("b1");
                return false;
            } else if (this.targetEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.targetEntity.squaredDistanceTo(this.mob)) {
                JerkSteve.LOGGER.info("b2");
                return false;
            } else {
                JerkSteve.LOGGER.info("c");
                jerkSteve.successfullyAttacked = false;
                this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.fleePath != null;
            }
        }
    }

    @Override
    public void start() {
        JerkSteve.LOGGER.info("d");
        this.fleeingEntityNavigation.startMovingAlong(this.fleePath, 0.1);
    }

    @Override
    public void stop() {
        JerkSteve.LOGGER.info("f");
        this.targetEntity = null;
    }

    @Override
    public boolean shouldContinue() {
        JerkSteve.LOGGER.info("e");
        return !this.fleeingEntityNavigation.isIdle();
    }
}
