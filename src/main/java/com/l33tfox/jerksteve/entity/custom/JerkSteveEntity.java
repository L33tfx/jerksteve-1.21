package com.l33tfox.jerksteve.entity.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JerkSteveEntity extends HostileEntity implements RangedAttackMob, InventoryOwner {

    private final Item.Settings settings = new Item.Settings();
    private final SimpleInventory inventory = new SimpleInventory(9);

    public JerkSteveEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        //inventory.addStack(itemStacks[0]);

//        for (Item item : items) {
//            inventory.addStack(new ItemStack(item, 64));
//        }
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(3, new FleeEntityGoal<>(this, PlayerEntity.class, 6.0F, 0.1, 0.13));
        goalSelector.add(5, new WanderNearTargetGoal(this, 0.1, 32.0F));
        goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 20.0F));
        goalSelector.add(6, new LookAroundGoal(this));
        targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
//        final ItemStack[] itemStacks = {
//                new ItemStack(new BowItem(settings), 1),
//                new ItemStack(new ArrowItem(settings), 64),
//                new ItemStack(new BlockItem(new AnvilBlock(AbstractBlock.Settings.create()), settings), 64),
//                new ItemStack(new EggItem(settings), 64),
//                new ItemStack(new ShearsItem(settings), 1),
//                new ItemStack(new AxeItem(ToolMaterials.DIAMOND, new Item.Settings()), 1),
//                new ItemStack(new ShovelItem(ToolMaterials.DIAMOND, new Item.Settings()), 1),
//                new ItemStack(new PickaxeItem(ToolMaterials.DIAMOND, new Item.Settings()), 1)
//        };

        //equipStack(EquipmentSlot.MAINHAND, new ItemStack(new BowItem(settings), 1));
        //inventory.addStack(new ItemStack(new BowItem(settings), 1));
        initEquipment(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        equipStack(EquipmentSlot.MAINHAND, new ItemStack(new BowItem(settings), 1));
        //equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {

    }

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }
}
