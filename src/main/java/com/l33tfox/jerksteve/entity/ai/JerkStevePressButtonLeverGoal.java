package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.GameRules;

import java.util.EnumSet;

import static net.minecraft.block.WallMountedBlock.FACE;

public class JerkStevePressButtonLeverGoal extends Goal {

    private final JerkSteveEntity jerkSteve;
    private BlockPos buttonOrLever;
    private int ticksSinceLastClick;
    private final float range;
    private final int clickTickRate;

    public JerkStevePressButtonLeverGoal(JerkSteveEntity jerkSteve, float range, int clickTickRate) {
        this.jerkSteve = jerkSteve;
        buttonOrLever = null;
        ticksSinceLastClick = 0;
        this.range = range;
        this.clickTickRate = clickTickRate;
        setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
    }

    @Override
    public void start() {
        ticksSinceLastClick = clickTickRate - 5;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = jerkSteve.getTarget();
        buttonOrLever = jerkSteve.getBlockInInteractionRange(Blocks.LEVER, BlockTags.BUTTONS);

        if (buttonOrLever == null) { // exit early for optimization
            return false;
        }

        Vec3d eyePos = jerkSteve.getEyePos();

        BlockHitResult raycastResult = jerkSteve.getWorld().raycast(
                new BlockStateRaycastContext(eyePos, Vec3d.of(buttonOrLever), state -> state.isIn(BlockTags.BUTTONS) || state.isOf(Blocks.LEVER)));
        boolean buttonOrLeverHit = false;

        // check if JerkSteve can see button/lever block directly or if other blocks are in the way
        if (raycastResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = raycastResult.getBlockPos();

            if (blockPos.equals(buttonOrLever)) {
                buttonOrLeverHit = true;
            }
        }

        return buttonOrLever != null && buttonOrLeverHit && jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                && target != null && jerkSteve.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= range * range;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void tick() {
        BlockState blockState = jerkSteve.getWorld().getBlockState(buttonOrLever);
        BlockFace face = blockState.get(FACE);

        if (face == BlockFace.FLOOR) {
            jerkSteve.getLookControl().lookAt(buttonOrLever.getX() + 0.5F, buttonOrLever.getY() - 1, buttonOrLever.getZ() + 0.5F, 30.0F, 30.0F);
        } else if (face == BlockFace.WALL) {
            jerkSteve.getLookControl().lookAt(buttonOrLever.getX() + 0.5F, buttonOrLever.getY() + 0.5F, buttonOrLever.getZ() + 0.5F, 30.0F, 30.0F);
        } else if (face == BlockFace.CEILING) {
            jerkSteve.getLookControl().lookAt(buttonOrLever.getX() + 0.5F, buttonOrLever.getY() + 1, buttonOrLever.getZ() + 0.5F, 30.0F, 30.0F);
        }

        if (ticksSinceLastClick % clickTickRate == 0 && ticksSinceLastClick != 0) {
            jerkSteve.swingHand(jerkSteve.getActiveHand());

            if (blockState.isOf(Blocks.LEVER)) {
                LeverBlock leverBlock = (LeverBlock) blockState.getBlock();
                leverBlock.togglePower(blockState, jerkSteve.getWorld(), buttonOrLever, null);
            } else if (blockState.isIn(BlockTags.BUTTONS)){
                ButtonBlock buttonBlock = (ButtonBlock) blockState.getBlock();
                buttonBlock.powerOn(blockState, jerkSteve.getWorld(), buttonOrLever, null);
            }

            ticksSinceLastClick = 0;
        } else {
            ticksSinceLastClick++;
        }
    }
}
