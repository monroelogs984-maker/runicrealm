package com.glennfo.runicrealm.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * Fast ambush hunter themed around Soul Rifts. Extends Zombie directly -
 * its normal melee AI/hostile targeting already matches "otherwise normal
 * behavior essentially," so no registerGoals override is needed at all,
 * just attribute values. Real "lives in/ambushes from rift terrain"
 * behavior isn't specially implemented (flagged simplification) - it's a
 * normal spawned hostile, not tied to rift geometry specifically yet.
 */
public class RiftStalker extends Zombie {
    public RiftStalker(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                // ~2x vanilla zombie's own 0.23 base movement speed.
                .add(Attributes.MOVEMENT_SPEED, 0.46)
                .add(Attributes.ATTACK_DAMAGE, 9.0);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // Deliberately bare-handed - no vanilla Zombie random gear/armor rolls.
    }
}
