package com.l33tfox.jerksteve.entity.custom;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.ai.JerkSteveBowAttackGoal;
import com.l33tfox.jerksteve.entity.ai.JerkSteveBreakBlockGoal;
import com.l33tfox.jerksteve.entity.ai.JerkSteveEggAttackGoal;
import com.l33tfox.jerksteve.entity.ai.JerkStevePlaceBlockGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class JerkSteveEntity extends HostileEntity implements RangedAttackMob, InventoryOwner {

    private final Item.Settings settings = new Item.Settings();
    public final SimpleInventory inventory = new SimpleInventory(9);

    private final ItemStack[] itemStacks = {
            new ItemStack(Items.BOW),
            new ItemStack(Items.ARROW),
            new ItemStack(Items.ANVIL),
            new ItemStack(Items.EGG),
            new ItemStack(Items.SHEARS),
            new ItemStack(Items.DIAMOND_AXE),
            new ItemStack(Items.DIAMOND_PICKAXE),
            new ItemStack(Items.DIAMOND_SHOVEL)
    };

    public JerkSteveEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        for (ItemStack stack : itemStacks) {
            inventory.addStack(stack);
        }
    }

    // copied from playerentity class
    public double getBlockInteractionRange() {
        return this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
    }

    // copied from playerentity class
    public boolean canInteractWithBlockAt(BlockPos pos, double additionalRange) {
        double d = this.getBlockInteractionRange() + additionalRange;
        return new Box(pos).squaredMagnitude(this.getEyePos()) < d * d;
    }

    @Override
    protected void initGoals() {
        JerkSteveEggAttackGoal<JerkSteveEntity> shootEggGoal = new JerkSteveEggAttackGoal<>(this, 1.0, 20, 15.0F);
        shootEggGoal.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
//        goalSelector.add(1, shootEggGoal);
        JerkSteveBowAttackGoal<JerkSteveEntity> shootBowGoal = new JerkSteveBowAttackGoal<>(this, 1.0, 20, 15.0F);
        shootBowGoal.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
//        goalSelector.add(1, shootBowGoal);
        JerkStevePlaceBlockGoal placeBlockGoal = new JerkStevePlaceBlockGoal(this);
        placeBlockGoal.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
//        goalSelector.add(1, placeBlockGoal);
        JerkSteveBreakBlockGoal breakBlockGoal = new JerkSteveBreakBlockGoal(this, 2);
        breakBlockGoal.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
        goalSelector.add(1, breakBlockGoal);
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(3, new FleeEntityGoal<>(this, PlayerEntity.class, 6.0F, 0.1, 0.13));
        goalSelector.add(5, new WanderNearTargetGoal(this, 0.1, 32.0F));
        goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        LookAtEntityGoal lookAtPlayerGoal = new LookAtEntityGoal(this, PlayerEntity.class, 20.0F);
        lookAtPlayerGoal.setControls(EnumSet.of(Goal.Control.LOOK));
        goalSelector.add(6, lookAtPlayerGoal);
        goalSelector.add(6, new LookAroundGoal(this));
        targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, 4.5)
                .add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, 3.0)
                .add(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)
                .add(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED)
                .add(EntityAttributes.PLAYER_SNEAKING_SPEED);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {

        //equipStack(EquipmentSlot.MAINHAND, new ItemStack(new BowItem(settings), 1));
        //inventory.addStack(new ItemStack(new BowItem(settings), 1));
        //itemList.set(0, new ItemStack(new BowItem(settings), 1));
        //itemList.set(0, ItemStack.EMPTY);
        initEquipment(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void lookAtBlockBelowEntity(Entity targetEntity) {
        lookAtEntity(targetEntity, 30.0F, 30.0F);

        double d = targetEntity.getX() - this.getX();
        double e = targetEntity.getZ() - this.getZ();
        double f = targetEntity.getBlockY() - this.getEyeY() - 1.5F;
        double g = Math.sqrt(d * d + e * e);

        float i = (float)(-(MathHelper.atan2(f, g) * 180.0F / (float)Math.PI));
        this.setPitch(changeAngle(this.getPitch(), i, 30.0F));
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        double random = Math.random();
        if (random >= 0.5) { // shoot arrow - copied from abstractskeletonentity class
            if (!activeItemStack.getItem().equals(Items.BOW)) {
                equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            }
//        if (getMainHandStack().getItem().equals(Items.BOW)) {
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            ItemStack itemStack2 = this.getProjectileType(itemStack);
            PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
            double d = target.getX() - this.getX();
            double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
            double f = target.getZ() - this.getZ();
            double g = Math.sqrt(d * d + f * f);
            persistentProjectileEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
            this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.getWorld().spawnEntity(persistentProjectileEntity);
        } else { // shoot egg - copied from snowgolementity class mostly
            equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.EGG));
            EggEntity eggEntity = new EggEntity(this.getWorld(), this);
            double d = target.getEyeY() - 1.1F;
            double e = target.getX() - this.getX();
            double f = d - eggEntity.getY();
            double g = target.getZ() - this.getZ();
            double h = Math.sqrt(e * e + g * g) * 0.2F;
            eggEntity.setVelocity(e, f + h, g, 1.6F, 1.0F);
            this.playSound(SoundEvents.ENTITY_EGG_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.getWorld().spawnEntity(eggEntity);
        }
    }

    // copied from abstractskeletonentity class
    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier, shotFrom);
    }

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }
}
