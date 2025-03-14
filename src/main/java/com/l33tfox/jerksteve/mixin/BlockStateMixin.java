package com.l33tfox.jerksteve.mixin;

import com.l33tfox.jerksteve.BlockStateDuck;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements BlockStateDuck {

    @Unique
    private JerkSteveEntity jerkSteveSpawned;

    public JerkSteveEntity jerksteve$getJerkSteve() {
        return jerkSteveSpawned;
    }

    public void jerksteve$setJerkSteve(JerkSteveEntity jerkSteve) {
        jerkSteveSpawned = jerkSteve;
    }
}
