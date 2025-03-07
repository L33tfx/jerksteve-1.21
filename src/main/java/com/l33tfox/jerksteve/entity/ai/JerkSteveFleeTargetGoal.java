package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

// Goal for JerkSteve to run away after successfully attacking the target player
public class JerkSteveFleeTargetGoal<T extends LivingEntity> extends FleeEntityGoal<T> {

    private final JerkSteveEntity jerkSteve;

    public JerkSteveFleeTargetGoal(JerkSteveEntity jerkSteve, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(jerkSteve, fleeFromType, distance, slowSpeed, fastSpeed);

        this.jerkSteve = jerkSteve;
        setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        targetEntity = (T) jerkSteve.getTarget();

        if (targetEntity == null || !jerkSteve.successfullyAttacked) { // if no target or JerkSteve didn't just successfully attack
            return false;
        }

        Vec3d vec3d = NoPenaltyTargeting.findFrom(jerkSteve, 30, 15, targetEntity.getPos()); // find a path away

        // if no path is found or the path goes closer to the target
        if (vec3d == null || targetEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < targetEntity.squaredDistanceTo(jerkSteve)) {
            return false;
        }

        jerkSteve.successfullyAttacked = false; // reset boolean, since it was previously true
        fleePath = fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);

        return fleePath != null;
    }

    @Override
    public void start() {
        fleeingEntityNavigation.startMovingAlong(fleePath, 0.1);
    }
}
