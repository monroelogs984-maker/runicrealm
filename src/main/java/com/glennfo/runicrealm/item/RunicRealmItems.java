package com.glennfo.runicrealm.item;

import com.glennfo.runicrealm.RunicRealm;
import com.glennfo.runicrealm.block.RunicRealmBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

    private RunicRealmItems() {}
}
