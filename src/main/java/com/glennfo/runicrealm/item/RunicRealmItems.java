package com.glennfo.runicrealm.item;

import com.glennfo.runicrealm.RunicRealm;
import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.glennfo.runicrealm.fluid.RunicRealmFluids;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RunicRealmItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RunicRealm.MODID);

    public static final RegistryObject<Item> SOUL_AND_STEEL = ITEMS.register("soul_and_steel",
            () -> new SoulAndSteelItem(new Item.Properties().durability(64)));

    public static final RegistryObject<Item> RUNIC_PORTAL_CRYSTAL = ITEMS.register("runic_portal_crystal",
            () -> new BlockItem(RunicRealmBlocks.RUNIC_PORTAL_CRYSTAL.get(), new Item.Properties()));

    public static final RegistryObject<Item> LUMINOUS_QUARTZ_ORE = ITEMS.register("luminous_quartz_ore",
            () -> new BlockItem(RunicRealmBlocks.LUMINOUS_QUARTZ_ORE.get(), new Item.Properties()));

    // The raw material Luminous Quartz Ore actually drops (mirrors vanilla quartz, not the
    // ore block itself).
    public static final RegistryObject<Item> LUMINOUS_QUARTZ = ITEMS.register("luminous_quartz",
            () -> new Item(new Item.Properties()));

    // Same BlockItem-with-food-properties pattern as vanilla Sweet Berries: right-clicking
    // a valid wall/ceiling plants a new Cave Root Vine strip (ordinary BlockItem
    // placement, since VineBlock's own getStateForPlacement handles the face logic),
    // right-clicking anywhere else eats it. 1.5 drumsticks = 3 nutrition (each icon is
    // 2 nutrition); 15s of Haste I (300 ticks, amplifier 0) on every eat.
    private static final FoodProperties CAVE_ROOT_FOOD = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 300, 0), 1.0F)
            .build();

    public static final RegistryObject<Item> CAVE_ROOT = ITEMS.register("cave_root",
            () -> new BlockItem(RunicRealmBlocks.CAVE_ROOT_VINE.get(), new Item.Properties().food(CAVE_ROOT_FOOD)));

    // Plain, non-edible placer for the vine block itself (matches vanilla's own separate
    // Vine item) - a second, distinct creative-menu entry alongside the food item above.
    // Both wrap the same block; only the loot table's own drop (cave_root) matters for
    // what breaking the vine actually gives you.
    public static final RegistryObject<Item> CAVE_ROOT_VINE = blockItem(RunicRealmBlocks.CAVE_ROOT_VINE);

    // Properties mirror vanilla's own water_bucket exactly (verified via javap):
    // craftRemainder(BUCKET), stacksTo(1). Lazy lambda supplier for the same
    // class-loading-order reason documented in RunicRealmFluids.
    public static final RegistryObject<Item> BIOLUMINESCENT_WATER_BUCKET = ITEMS.register(
            "bioluminescent_water_bucket", () -> new BucketItem(() -> RunicRealmFluids.BIOLUMINESCENT_WATER.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> RUNIC_CRYSTAL_CLUSTER = blockItem(RunicRealmBlocks.RUNIC_CRYSTAL_CLUSTER);

    // The raw material each cluster block drops - a Speleothem crafting resource.
    public static final RegistryObject<Item> RUNIC_CRYSTAL = ITEMS.register("runic_crystal",
            () -> new Item(new Item.Properties()));

    public static final Map<String, RegistryObject<Item>> GLOW_MUSHROOM_ITEMS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Item>> GLOWING_MYCELIUM_ITEMS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Item>> GLOW_MUSHROOM_CAP_ITEMS = new LinkedHashMap<>();
    static {
        RunicRealmBlocks.GLOW_MUSHROOMS.forEach((color, block) -> GLOW_MUSHROOM_ITEMS.put(color, blockItem(block)));
        RunicRealmBlocks.GLOWING_MYCELIUM.forEach((color, block) -> GLOWING_MYCELIUM_ITEMS.put(color, blockItem(block)));
        RunicRealmBlocks.GLOW_MUSHROOM_CAPS.forEach((color, block) -> GLOW_MUSHROOM_CAP_ITEMS.put(color, blockItem(block)));
    }

    public static final RegistryObject<Item> GLOW_MUSHROOM_STEM = blockItem(RunicRealmBlocks.GLOW_MUSHROOM_STEM);

    private static RegistryObject<Item> blockItem(RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private RunicRealmItems() {}
}
