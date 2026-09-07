package com.glennfo.runicrealm.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * The dimension's light-emitting mob - Glenn's reframe of "floating light
 * motes" into an actual creature. There's no per-entity dynamic light in
 * this Forge version, so the glow is faked the standard way: an invisible
 * vanilla minecraft:light block (a real block, MAX_LEVEL 15 IntegerProperty
 * LEVEL - confirmed via javap, not guessed) that follows the entity's block
 * position, placed/removed as it moves. No custom model/texture - the
 * renderer draws nothing at all, so what you actually see is just the
 * light block drifting around, which already reads as "a small glowing
 * thing floating in the dark" without needing any art.
 *
 * AI is generic flying-wander (FlyingMoveControl + FlyingPathNavigation +
 * WaterAvoidingRandomFlyingGoal, the same building blocks vanilla bees/
 * parrots use), not a copy of Bat's own more specialized hard-coded flight
 * logic - simpler to get right and good enough for "drifts around glowing."
 * Fully passive: no attack goals, harmless.
 */
public class Firefly extends PathfinderMob {
    private BlockPos lightPos;

    public Firefly(EntityType<? extends Firefly> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount % 5 == 0) {
            updateLight();
        }
    }

    private void updateLight() {
        BlockPos pos = this.blockPosition();
        if (pos.equals(lightPos)) {
            return;
        }
        Level level = this.level();
        if (lightPos != null && level.getBlockState(lightPos).is(Blocks.LIGHT)) {
            level.removeBlock(lightPos, false);
        }
        if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 7), 3);
            lightPos = pos.immutable();
        } else {
            lightPos = null;
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide && lightPos != null && this.level().getBlockState(lightPos).is(Blocks.LIGHT)) {
            this.level().removeBlock(lightPos, false);
        }
        super.remove(reason);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return true;
    }
}
