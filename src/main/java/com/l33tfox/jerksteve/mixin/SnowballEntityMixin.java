package com.l33tfox.jerksteve.mixin;

import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public abstract class SnowballEntityMixin extends ThrownItemEntity {

    public SnowballEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    // Make snowball do damage and knockback to players (only when it is thrown by a JerkSteveEntity)
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void onPlayerHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (getOwner() instanceof JerkSteveEntity jerkSteve && entityHitResult.getEntity() instanceof PlayerEntity player && !player.getAbilities().invulnerable) {
            if (player.getWorld() instanceof ServerWorld world) {
                double e = Math.max(0.0, 1.0 - player.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                Vec3d vec3d = getVelocity().multiply(1.0, 0.0, 1.0).normalize();
                if (vec3d.lengthSquared() > 0.0) {
                    player.addVelocity(vec3d.x, 0.1, vec3d.z);
                }

                player.damage(getDamageSources().thrown(jerkSteve, player), 0.5f);
                jerkSteve.snowballLanded = true; // used to determine if attack was successful and JerkSteve should run
            }
        }
    }
}
