package com.l33tfox.jerksteve.entity.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class JerkSteveUtil {
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
}
