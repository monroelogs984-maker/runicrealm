package com.glennfo.runicrealm.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

/**
 * Fast, short, hard-to-hit skirmisher. Extends Silverfish directly for its
 * already-small hitbox and quick, erratic movement - matches "fast but
 * short, hard to hit" closely without any custom AI needed, just stats.
 * Real "ambushes from Bioluminescent Pools" behavior isn't implemented
 * (flagged simplification): vanilla mob water-AI keys off
 * minecraft:water, and this dimension's fluid is deliberately its own
 * separate FluidType, not tagged into that - Silverfish's own
 * stone-merge ambush mechanic also doesn't translate to a fluid at all.
 * For now this is a normal fast small hostile, not pool-specific.
 */
public class PoolLurker extends Silverfish {
    public PoolLurker(EntityType<? extends Silverfish> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 3.0);
    }
}
