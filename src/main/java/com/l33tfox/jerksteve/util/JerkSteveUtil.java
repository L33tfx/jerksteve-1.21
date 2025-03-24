package com.l33tfox.jerksteve.util;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class JerkSteveUtil {

    public static final float BOW_ATTACK_HEALTH_THRESHOLD = 5.0F;

    // Return the best item to use to mine a certain block from a list of possible items
    public static Item getToolToMine(LivingEntity entity, BlockPos blockPos, Item[] possibleItems) {
        BlockState blockState = entity.getWorld().getBlockState(blockPos);

        float maxSpeed = 0F;
        Item bestItem = null;

        for (Item item : possibleItems) {
            if (item.getMiningSpeed(new ItemStack(item), blockState) >= maxSpeed) {
                maxSpeed = item.getMiningSpeed(new ItemStack(item), blockState);
                bestItem = item;
            }
        }

        return bestItem;
    }

    // Return true if an entity cannot collide with blockState (will fall through it), false if it can
    public static boolean isNotCollidable(BlockState blockState) {
        return blockState.isAir()
                || blockState.isLiquid()
                || blockState.isIn(BlockTags.FIRE)
                || !blockState.isSolid();
    }

    public static int roundToBlock(double coord) {
        if (coord >= 0) {
            return (int) Math.ceil(coord);
        } else {
            return (int) Math.floor(coord);
        }
    }

    public static BlockPos posXBelow(LivingEntity target, int yDrop) {
        return posXBelow(target, 0, yDrop, 0);
    }

    public static BlockPos posXBelow(LivingEntity target, int xDisplacement, int yDrop, int zDisplacement) {
        if (target == null) {
            return null;
        }

        return new BlockPos(target.getBlockX() + xDisplacement, target.getBlockY() - yDrop, target.getBlockZ() + zDisplacement);
    }
}
