package com.glennfo.runicrealm.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * A Skeleton variant that wields a stone pickaxe instead of a bow. No new AI
 * needed: AbstractSkeleton already carries both a bowGoal and a meleeGoal,
 * and its own setItemSlot() override calls reassessWeaponGoal() on every
 * equipment change (verified via javap) - that method swaps to meleeGoal
 * automatically whenever the mainhand item isn't a BowItem. Overriding just
 * the default-equipment step is enough to make this a genuine melee fighter,
 * not merely a reskinned bow-user.
 */
public class RunicMinerSkeleton extends Skeleton {
    public RunicMinerSkeleton(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_PICKAXE));
    }
}
