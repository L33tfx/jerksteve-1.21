package com.l33tfox.jerksteve.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WanderNearTargetGoal.class)
public interface WanderNearTargetGoalAccessor {

    @Accessor("x")
    double getTargetX();

    @Accessor("y")
    double getTargetY();

    @Accessor("z")
    double getTargetZ();

    @Accessor("target")
    LivingEntity getTarget();

    @Accessor("x")
    void setTargetX(double x);

    @Accessor("y")
    void setTargetY(double y);

    @Accessor("z")
    void setTargetZ(double z);

    @Accessor("target")
    void setTarget(LivingEntity target);
}
