package com.l33tfox.jerksteve;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import org.spongepowered.asm.mixin.Unique;

public interface BlockStateDuck {

    public JerkSteveEntity jerksteve$getJerkSteve();
    public void jerksteve$setJerkSteve(JerkSteveEntity jerkSteve);

}
