package com.l33tfox.jerksteve.entity.custom;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.ai.*;
import com.l33tfox.jerksteve.util.JerkSteveUtil;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class JerkSteveEntity extends HostileEntity implements RangedAttackMob, InventoryOwner {

    public final SimpleInventory inventory = new SimpleInventory(9);
    public boolean successfullyAttacked = false; // tracks if last attempted attack harmed target
    public boolean snowballLanded = false; // tracks if last thrown snowball connected with target

    // items JerkSteve can equip
    public static final Item[] items = {
            Items.BOW,
            Items.ARROW,
            Items.ANVIL,
            Items.SNOWBALL,
            Items.SHEARS,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_SHOVEL,
            Items.AIR
    };

    public JerkSteveEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        for (Item item : items) {
            inventory.addStack(new ItemStack(item));
        }
    }

    // copied from playerentity class - get how far JerkSteve can mine blocks from
    public double getBlockInteractionRange() {
        return getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
    }

    // copied from playerentity class - return if JerkSteve is close enough to mine a block
    public boolean canInteractWithBlockAt(BlockPos pos, double additionalRange) {
        double d = getBlockInteractionRange() + additionalRange;
        return new Box(pos).squaredMagnitude(getEyePos()) < d * d;
    }

    @Override
    protected void initGoals() {
        // controls are set in the goal constructors, so that the goals cannot happen at the same time
        // (lower priority goals will interrupt higher priority goals using the same control(s))

        // ordering/priority of goals is determined by their actual priorities here, control usage, and explicit checks
        // in goal canStart() or shouldContinue() methods

        goalSelector.add(0, new JerkSteveFleeTargetGoal<>(this, PlayerEntity.class, 20.0F, 3.75, 4));
        goalSelector.add(1, new JerkSteveBreakBlockGoal(this));
//        goalSelector.add(1, new JerkStevePlaceBlockGoal(this));
        goalSelector.add(2, new JerkSteveSnowballAttackGoal<>(this, 1.0, 20, 15.0F));
        goalSelector.add(2, new JerkSteveBowAttackGoal<>(this, 1.0, 20, 15.0F));
        goalSelector.add(2, new SwimGoal(this));
        goalSelector.add(3, new JerkStevePressButtonLeverGoal(this, 10.0F, 15));
        goalSelector.add(4, new JerkSteveFollowTargetGoal(this, 3.5, true));
        goalSelector.add(5, new WanderNearTargetGoal(this, 3.5, 15.0F));
        goalSelector.add(5, new LookAroundGoal(this));
//        goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        LookAtEntityGoal lookAtPlayerGoal = new LookAtEntityGoal(this, PlayerEntity.class, 50.0F);
        lookAtPlayerGoal.setControls(EnumSet.of(Goal.Control.LOOK));
        goalSelector.add(6, lookAtPlayerGoal);

        targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    // Add JerkSteve's EntityAttributes
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 128)
                .add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, 4.5)
                .add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, 3.0)
                .add(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)
                .add(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED)
                .add(EntityAttributes.PLAYER_SNEAKING_SPEED);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        initEquipment(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
    }

    @Override
    public void tick() {
        heal(getMaxHealth() - getHealth());

        if (getTarget() == null) { // if loses track of target
            setTarget(getWorld().getClosestPlayer(this, 40F));
        }

        super.tick();
    }

    // called in the tick() of snowballattackgoal and bowattackgoal
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        double random = Math.random();
        if (getMainHandStack().getItem().equals(Items.BOW)) { // shoot arrow - copied from abstractskeletonentity mostly
            ItemStack itemStack = getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            ItemStack itemStack2 = getProjectileType(itemStack);
            PersistentProjectileEntity persistentProjectileEntity = createArrowProjectile(itemStack2, pullProgress, itemStack);
            double d = target.getX() - getX();
            double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
            double f = target.getZ() - getZ();
            double g = Math.sqrt(d * d + f * f);
            persistentProjectileEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - getWorld().getDifficulty().getId() * 4));
            playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
            getWorld().spawnEntity(persistentProjectileEntity);
        } else { // shoot egg - copied from snowgolementity class mostly
            equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SNOWBALL));
            SnowballEntity snowballEntity = new SnowballEntity(getWorld(), this);
            double d = target.getEyeY() - 1.3F;
            double e = target.getX() - getX();
            double f = d - snowballEntity.getY();
            double g = target.getZ() - getZ();
            double h = Math.sqrt(e * e + g * g) * 0.2F;
            snowballEntity.setVelocity(e, f + h, g, 1.6F, 0.0F);
            playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 0.4F / (getRandom().nextFloat() * 0.4F + 0.8F));
            getWorld().spawnEntity(snowballEntity);
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

    // Make name plate render above JerkSteve
    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    // Returns true if there is a drop of more than 2 blocks within 2 blocks of the target
    public boolean isTargetNearDrop() {
        LivingEntity target = getTarget();

        if (target == null || !target.isOnGround()) {
            return false;
        }

        // if target is sneaking on edge of block
        if (target.isOnGround() && target.isSneaking()
                && JerkSteveUtil.isNotCollidable(getWorld().getBlockState(JerkSteveUtil.posXBelow(target, 1)))) {
            return true;
        }

        for (int xDisplace = -2; xDisplace <= 2; xDisplace++) { // iterate through 5x5 grid of blocks at level of block target is standing on
            for (int zDisplace = -2; zDisplace <= 2; zDisplace++) {
                BlockPos blockPos = JerkSteveUtil.posXBelow(target, xDisplace, 1, zDisplace);
                BlockState blockState = getWorld().getBlockState(blockPos);

                boolean blockIsNotCollidable = JerkSteveUtil.isNotCollidable(blockState);

                // if next to 1 block deep water, lava, or fire, return true so that snowball attack starts
                if (blockIsNotCollidable && blockState.isLiquid() || blockState.isIn(BlockTags.FIRE)) {
                    return true;
                // if block is not collidable but is just air, tall grass, etc
                } else if (blockIsNotCollidable) {
                    BlockPos blockPos2 = blockPos.add(0, -1, 0);

                    // check that the drop is at least 2 blocks deep
                    if (JerkSteveUtil.isNotCollidable(getWorld().getBlockState(blockPos2))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Finds and returns the BlockPos of a block within a 5x5x5 cube of JerkSteve of type block or with key blockTagKey.
    // If no block is found, returns null
    public BlockPos getBlockInInteractionRange(Block block, TagKey<Block> blockTagKey) {
        for (int xDisplace = -2; xDisplace <= 2; xDisplace++) {
            for (int yDisplace = -2; yDisplace <= 2; yDisplace++) {
                for (int zDisplace = -2; zDisplace <= 2; zDisplace++) {
                    BlockPos blockPos = getBlockPos().add(xDisplace, yDisplace + 1, zDisplace);
                    BlockState blockState = getWorld().getBlockState(blockPos);

                    if ((blockState.isOf(block) || (blockTagKey != null && blockState.isIn(blockTagKey))) && canInteractWithBlockAt(blockPos, 0)) {
                        return blockPos;
                    }
                }
            }
        }

        return null;
    }

    // adapted from tameableentity class (used for wolf followownergoal)
    public void tryTeleportNearTarget() {
        LivingEntity target = getTarget();

        if (target == null) {
            return;
        }

        BlockPos pos = target.getBlockPos();
        tryTeleportNear(pos);
    }

    public void tryTeleportNear(Vec3i vec3i) {
        for (int i = 0; i < 10; i++) {
            int j = random.nextBetween(-3, 3);
            int k = random.nextBetween(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = random.nextBetween(-1, 1);
                if (tryTeleportTo(vec3i.getX() + j, vec3i.getY() + l, vec3i.getZ() + k)) {
                    return;
                }
            }
        }
    }

    // adapted from tameableentity class (used for wolf followownergoal)
    private boolean tryTeleportTo(int x, int y, int z) {
        if (!canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        }

        refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, getYaw(), getPitch());
        navigation.stop();
        return true;
    }

    // adapted from tameableentity class (used for wolf followownergoal)
    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this, pos);
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        }

        BlockPos blockPos = pos.subtract(this.getBlockPos());
        return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(blockPos));
    }
}
