package com.l33tfox.jerksteve.entity.ai;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.entity.util.JerkSteveUtil;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.WorldEvents;

import java.util.EnumSet;

public class JerkStevePressButtonLeverGoal extends Goal {

    private final JerkSteveEntity jerkSteve;
    private BlockPos buttonOrLever;
    private int ticksSinceLastClick;
    private float range;
    private int clickTickRate;

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
        ticksSinceLastClick = 0;
    }

    @Override
    public boolean canStart() {
        JerkSteve.LOGGER.info("in canStart");
        LivingEntity target = jerkSteve.getTarget();
        buttonOrLever = jerkSteve.getBlockInInteractionRange(Blocks.LEVER, BlockTags.BUTTONS);

        if (buttonOrLever == null) { // exit early for optimization
            return false;
        }

        Vec3d eyePos = jerkSteve.getEyePos();

        BlockHitResult raycastResult = jerkSteve.getWorld().raycast(new BlockStateRaycastContext(eyePos, Vec3d.of(buttonOrLever), state -> state.isIn(BlockTags.BUTTONS) || state.isOf(Blocks.LEVER)));
        boolean buttonOrLeverHit = false;

        // check if JerkSteve can see button/lever directly, or other blocks are in the way
        JerkSteve.LOGGER.info("" + raycastResult.getType());
        if (raycastResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = raycastResult.getBlockPos();
            JerkSteve.LOGGER.info("" + jerkSteve.getWorld().getBlockState(blockPos).getBlock());

            if (blockPos.equals(buttonOrLever)) {
                buttonOrLeverHit = true;
            }
        }

        JerkSteve.LOGGER.info("button/lever hit: " + buttonOrLeverHit);

        return buttonOrLever != null && buttonOrLeverHit && jerkSteve.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                && target != null && jerkSteve.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= range * range;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void tick() {
        LivingEntity target = jerkSteve.getTarget();
        JerkSteve.LOGGER.info("in tick");

        // look at button/lever block
        jerkSteve.getLookControl().lookAt(buttonOrLever.getX(), buttonOrLever.getY() + 0.5F, buttonOrLever.getZ(), 30.0F, 30.0F);

        if (ticksSinceLastClick % clickTickRate == 0 && ticksSinceLastClick != 0) {
            jerkSteve.swingHand(jerkSteve.getActiveHand());

            BlockState blockState = jerkSteve.getWorld().getBlockState(buttonOrLever);

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
