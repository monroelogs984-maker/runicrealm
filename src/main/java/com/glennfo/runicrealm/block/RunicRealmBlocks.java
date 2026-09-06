package com.glennfo.runicrealm.block;

import com.glennfo.runicrealm.RunicRealm;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RunicRealmBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RunicRealm.MODID);

    // Properties mirror vanilla Blocks.SOUL_FIRE exactly (verified via javap against
    // the official-mapped 1.20.1 jar) - only canSurvive/onPlace/tick differ.
    public static final RegistryObject<Block> ETERNAL_SOUL_FIRE = BLOCKS.register("eternal_soul_fire",
            () -> new EternalSoulFireBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 10)
                    .sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY)));

    // Placeholder tuning - obsidian-adjacent toughness with a faint glow, since it's
    // meant to be a mystical portal-frame material. Adjust once the frame is built.
    public static final RegistryObject<Block> RUNIC_PORTAL_CRYSTAL = BLOCKS.register("runic_portal_crystal",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 7)));

    // Properties mirror vanilla Blocks.NETHER_PORTAL exactly (verified via javap):
    // unbreakable (-1 strength), no collision, glassy sound, light level 11.
    public static final RegistryObject<Block> RUNIC_PORTAL = BLOCKS.register("runic_portal",
            () -> new RunicPortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 11)
                    .pushReaction(PushReaction.BLOCK)));

    // Ore-like light source scattered through the dimension for baseline visibility.
    // Placeholder texture reuses vanilla glowstone pending custom art.
    public static final RegistryObject<Block> LUMINOUS_QUARTZ_ORE = BLOCKS.register("luminous_quartz_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(3.0F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 13)));

    // First batch of glow mushroom colors - cyan/purple/blue to match the dimension's
    // established mystical palette (portal blue, crystal purple). More colors later.
    // Properties otherwise mirror vanilla small mushrooms (noCollission, instabreak,
    // SoundType.GRASS) but with a strong light level instead of none.
    public static final RegistryObject<Block> GLOW_MUSHROOM_CYAN = glowMushroom("cyan", MapColor.COLOR_CYAN);
    public static final RegistryObject<Block> GLOWING_MYCELIUM_CYAN = glowingMycelium("cyan", MapColor.COLOR_CYAN);
    public static final RegistryObject<Block> GLOW_MUSHROOM_PURPLE = glowMushroom("purple", MapColor.COLOR_PURPLE);
    public static final RegistryObject<Block> GLOWING_MYCELIUM_PURPLE = glowingMycelium("purple", MapColor.COLOR_PURPLE);
    public static final RegistryObject<Block> GLOW_MUSHROOM_BLUE = glowMushroom("blue", MapColor.COLOR_BLUE);
    public static final RegistryObject<Block> GLOWING_MYCELIUM_BLUE = glowingMycelium("blue", MapColor.COLOR_BLUE);

    private static RegistryObject<Block> glowMushroom(String color, MapColor mapColor) {
        return BLOCKS.register("glow_mushroom_" + color, () -> new GlowMushroomBlock(BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .lightLevel(state -> 15)
                .pushReaction(PushReaction.DESTROY)));
    }

    private static RegistryObject<Block> glowingMycelium(String color, MapColor mapColor) {
        return BLOCKS.register("glowing_mycelium_" + color, () -> new Block(BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(0.6F)
                .sound(SoundType.GRASS)
                .lightLevel(state -> 6)));
    }

    private RunicRealmBlocks() {}
}
