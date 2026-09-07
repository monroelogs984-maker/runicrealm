package com.glennfo.runicrealm.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Slow, high-HP tank archetype - a full suit of diamond armor as
 * "corrupted armour," visually and mechanically reinforcing the tank role
 * (armor points on top of the raw HP pool). Extends Zombie for its normal
 * melee AI, same as Rift Stalker; only the equipment/attributes differ.
 * Drop chance for the armor is deliberately zeroed - free diamond gear on
 * every kill would undercut the "full Netherite just to get in" gate.
 */
public class CorruptedArmor extends Zombie {
    public CorruptedArmor(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 180.0)
                // roughly half vanilla zombie's own 0.23 base movement speed - slow tank.
                .add(Attributes.MOVEMENT_SPEED, 0.115)
                .add(Attributes.ATTACK_DAMAGE, 7.0);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, 0.0F);
        }
    }
}
