package com.glennfo.runicrealm.block;

import com.glennfo.runicrealm.RunicRealm;
import com.glennfo.runicrealm.fluid.RunicRealmFluids;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

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
    // Placeholder texture reuses vanilla glowstone pending custom art. DropExperienceBlock
    // is vanilla's own Nether Quartz Ore class (verified via javap) - reused directly so
    // it drops XP on mining like a real ore; 6-15 is 3x Nether Quartz's own 2-5 range
    // (also verified via javap, not guessed).
    public static final RegistryObject<Block> LUMINOUS_QUARTZ_ORE = BLOCKS.register("luminous_quartz_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(3.0F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 13),
                    UniformInt.of(6, 15)));

    // Reuses vanilla VineBlock directly (same attachment/spread mechanics as the real
    // vines block, per Glenn's ask) with vanilla's own exact properties (verified via
    // javap): mapColor PLANT, replaceable, noCollission, randomTicks, strength 0.2,
    // SoundType.VINE, ignitedByLava, pushReaction DESTROY. Only the loot table differs
    // (always drops an item here, vanilla vines need shears).
    public static final RegistryObject<Block> CAVE_ROOT_VINE = BLOCKS.register("cave_root_vine",
            () -> new VineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .replaceable()
                    .noCollission()
                    .randomTicks()
                    .strength(0.2F)
                    .sound(SoundType.VINE)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)));

    // Properties mirror vanilla Blocks.WATER exactly (verified via javap): mapColor WATER,
    // replaceable, noCollission, strength 100, pushReaction DESTROY, noLootTable, liquid(),
    // SoundType.EMPTY. Lazy lambda supplier, not a bare RegistryObject reference - see
    // RunicRealmFluids' class-loading-order note.
    public static final RegistryObject<LiquidBlock> BIOLUMINESCENT_WATER_BLOCK = BLOCKS.register(
            "bioluminescent_water", () -> new LiquidBlock(() -> RunicRealmFluids.BIOLUMINESCENT_WATER.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WATER)
                            .replaceable()
                            .noCollission()
                            .strength(100.0F)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY)));

    // Full 16-color vanilla dye palette (MapColor equivalents per color, verified against
    // vanilla wool/dye block registrations via javap). Keyed by DyeColor's serialized name.
    public static final Map<String, MapColor> DYE_MAP_COLORS = new LinkedHashMap<>();
    static {
        DYE_MAP_COLORS.put("white", MapColor.SNOW);
        DYE_MAP_COLORS.put("orange", MapColor.COLOR_ORANGE);
        DYE_MAP_COLORS.put("magenta", MapColor.COLOR_MAGENTA);
        DYE_MAP_COLORS.put("light_blue", MapColor.COLOR_LIGHT_BLUE);
        DYE_MAP_COLORS.put("yellow", MapColor.COLOR_YELLOW);
        DYE_MAP_COLORS.put("lime", MapColor.COLOR_LIGHT_GREEN);
        DYE_MAP_COLORS.put("pink", MapColor.COLOR_PINK);
        DYE_MAP_COLORS.put("gray", MapColor.COLOR_GRAY);
        DYE_MAP_COLORS.put("light_gray", MapColor.COLOR_LIGHT_GRAY);
        DYE_MAP_COLORS.put("cyan", MapColor.COLOR_CYAN);
        DYE_MAP_COLORS.put("purple", MapColor.COLOR_PURPLE);
        DYE_MAP_COLORS.put("blue", MapColor.COLOR_BLUE);
        DYE_MAP_COLORS.put("brown", MapColor.COLOR_BROWN);
        DYE_MAP_COLORS.put("green", MapColor.COLOR_GREEN);
        DYE_MAP_COLORS.put("red", MapColor.COLOR_RED);
        DYE_MAP_COLORS.put("black", MapColor.COLOR_BLACK);
    }

    // Full palette of glow mushrooms + matching glowing mycelium. Properties otherwise
    // mirror vanilla small mushrooms (noCollission, instabreak, SoundType.GRASS) but with
    // a strong light level instead of none.
    public static final Map<String, RegistryObject<Block>> GLOW_MUSHROOMS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Block>> GLOWING_MYCELIUM = new LinkedHashMap<>();
    static {
        DYE_MAP_COLORS.forEach((color, mapColor) -> {
            GLOW_MUSHROOMS.put(color, glowMushroom(color, mapColor));
            GLOWING_MYCELIUM.put(color, glowingMycelium(color, mapColor));
        });
    }

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

    // Giant mushroom cap (per color) + shared stem. HugeMushroomBlock is vanilla's own
    // generic class (no color-specific logic, just 6 boolean face properties for cap-vs-
    // pore texture) - reused directly rather than writing a new Block class. Properties
    // mirror vanilla's RED_MUSHROOM_BLOCK/MUSHROOM_STEM exactly (verified via javap):
    // strength 0.2, SoundType.WOOD, NoteBlockInstrument.BASS, ignitedByLava. Light levels
    // (cap bright, stem dim) are our own addition - vanilla giant mushrooms don't glow.
    public static final Map<String, RegistryObject<Block>> GLOW_MUSHROOM_CAPS = new LinkedHashMap<>();
    static {
        DYE_MAP_COLORS.forEach((color, mapColor) ->
                GLOW_MUSHROOM_CAPS.put(color, BLOCKS.register("glow_mushroom_block_" + color,
                        () -> new HugeMushroomBlock(BlockBehaviour.Properties.of()
                                .mapColor(mapColor)
                                .instrument(NoteBlockInstrument.BASS)
                                .strength(0.2F)
                                .sound(SoundType.WOOD)
                                .lightLevel(state -> 14)
                                .ignitedByLava()))));
    }

    public static final RegistryObject<Block> GLOW_MUSHROOM_STEM = BLOCKS.register("glow_mushroom_stem",
            () -> new HugeMushroomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOL)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(0.2F)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 8)
                    .ignitedByLava()));

    private RunicRealmBlocks() {}
}
