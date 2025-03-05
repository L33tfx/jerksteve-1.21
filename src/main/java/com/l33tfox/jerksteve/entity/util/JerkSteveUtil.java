package com.l33tfox.jerksteve.entity.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class JerkSteveUtil {

    public static final float bowAttackHealthThreshold = 5.0F;

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

    public static boolean isNotCollidable(BlockState blockState) {
        return blockState.isAir() || blockState.isLiquid() || blockState.isIn(BlockTags.FIRE);
    }

    public static int roundToBlock(double coord) {
        if (coord >= 0) {
            return (int) Math.ceil(coord);
        } else {
            return (int) Math.floor(coord);
        }
    }

    public static BlockPos posXBelow(LivingEntity target, int yDrop) {
        if (target == null) {
            return null;
        }

        return new BlockPos(roundToBlock(target.getX()), roundToBlock(target.getY() - yDrop), roundToBlock(target.getZ()));
    }

    public static BlockPos posXBelow(LivingEntity target, int xDisplacement, int yDrop, int zDisplacement) {
        if (target == null) {
            return null;
        }

        return new BlockPos(roundToBlock(target.getX() + xDisplacement), roundToBlock(target.getY() - yDrop), roundToBlock(target.getZ() + zDisplacement));
    }
}
