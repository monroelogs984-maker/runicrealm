package com.glennfo.runicrealm.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;

/**
 * Fast, small-hitbox flying attacker - the hostile inversion of Firefly.
 * Extends Vex directly: its existing teleport-and-charge flying combat AI
 * already matches "small hitbox b*tch that hits and darts away," so (like
 * Rift Stalker) no registerGoals override needed, just stats. Real "drawn
 * to light sources / snuffs them out" flavor isn't implemented yet
 * (flagged simplification) - it's Vex's normal random-teleport aggression.
 */
public class CorruptedWisp extends Vex {
    public CorruptedWisp(EntityType<? extends Vex> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Vex.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
    }
}
