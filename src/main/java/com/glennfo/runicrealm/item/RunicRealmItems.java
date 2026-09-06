package com.glennfo.runicrealm.item;

import com.glennfo.runicrealm.RunicRealm;
import com.glennfo.runicrealm.block.RunicRealmBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RunicRealmItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RunicRealm.MODID);

    public static final RegistryObject<Item> SOUL_AND_STEEL = ITEMS.register("soul_and_steel",
            () -> new SoulAndSteelItem(new Item.Properties().durability(64)));

    public static final RegistryObject<Item> RUNIC_PORTAL_CRYSTAL = ITEMS.register("runic_portal_crystal",
            () -> new BlockItem(RunicRealmBlocks.RUNIC_PORTAL_CRYSTAL.get(), new Item.Properties()));

    public static final RegistryObject<Item> LUMINOUS_QUARTZ_ORE = ITEMS.register("luminous_quartz_ore",
            () -> new BlockItem(RunicRealmBlocks.LUMINOUS_QUARTZ_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLOW_MUSHROOM_CYAN = blockItem(RunicRealmBlocks.GLOW_MUSHROOM_CYAN);
    public static final RegistryObject<Item> GLOWING_MYCELIUM_CYAN = blockItem(RunicRealmBlocks.GLOWING_MYCELIUM_CYAN);
    public static final RegistryObject<Item> GLOW_MUSHROOM_PURPLE = blockItem(RunicRealmBlocks.GLOW_MUSHROOM_PURPLE);
    public static final RegistryObject<Item> GLOWING_MYCELIUM_PURPLE = blockItem(RunicRealmBlocks.GLOWING_MYCELIUM_PURPLE);
    public static final RegistryObject<Item> GLOW_MUSHROOM_BLUE = blockItem(RunicRealmBlocks.GLOW_MUSHROOM_BLUE);
    public static final RegistryObject<Item> GLOWING_MYCELIUM_BLUE = blockItem(RunicRealmBlocks.GLOWING_MYCELIUM_BLUE);

    private static RegistryObject<Item> blockItem(RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private RunicRealmItems() {}
}
