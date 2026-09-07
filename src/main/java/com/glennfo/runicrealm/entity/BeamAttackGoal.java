package com.glennfo.runicrealm.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * A simple hitscan "beam" ranged attack: no projectile entity, just direct
 * damage on cooldown once in range and line of sight, with a particle line
 * for visual feedback. Used by Speleothem Guardian.
 */
public class BeamAttackGoal extends Goal {
    private final Mob mob;
    private final double range;
    private final float damage;
    private final int cooldownTicks;
    private int cooldown;

    public BeamAttackGoal(Mob mob, double range, float damage, int cooldownTicks) {
        this.mob = mob;
        this.range = range;
        this.damage = damage;
        this.cooldownTicks = cooldownTicks;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && mob.distanceToSqr(target) <= range * range;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return;
        }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (mob.hasLineOfSight(target)) {
            fireBeam(target);
            cooldown = cooldownTicks;
        }
    }

    private void fireBeam(LivingEntity target) {
        target.hurt(mob.level().damageSources().indirectMagic(mob, mob), damage);
        if (mob.level() instanceof ServerLevel serverLevel) {
            Vec3 start = mob.getEyePosition();
            Vec3 end = target.getEyePosition();
            int steps = 20;
            for (int i = 0; i <= steps; i++) {
                Vec3 p = start.lerp(end, i / (double) steps);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
}
