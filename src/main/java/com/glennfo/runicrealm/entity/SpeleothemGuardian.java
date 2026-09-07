package com.glennfo.runicrealm.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A tough, slow ambusher that lurks around Crystal Speleothems - not a
 * literal block-disguise (that's a real simplification, flagged: it's a
 * normal spawned mob, not a speleothem block that wakes up), but the
 * stats/combat role are exactly as specced. Extends IronGolem directly to
 * get its melee-attack goal, model, and IronGolemRenderer for free -
 * vanilla IronGolem is neutral by default (only fights when provoked or
 * defending villagers), so a real hostile-targeting goal is added here to
 * make it a genuine unprovoked threat. The beam attack is layered on top
 * via BeamAttackGoal (4 damage, 80-tick/4s cooldown, 12-block range).
 */
public class SpeleothemGuardian extends IronGolem {
    public SpeleothemGuardian(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 125.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new BeamAttackGoal(this, 12.0, 4.0F, 80));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
