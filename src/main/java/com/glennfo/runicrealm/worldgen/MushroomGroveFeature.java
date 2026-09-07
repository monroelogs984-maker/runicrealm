package com.glennfo.runicrealm.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

/**
 * Places a vegetation_patch (mycelium + scattered small mushrooms) and a
 * huge_red_mushroom at the same origin in one worldgen pass, so a giant
 * mushroom is attempted every time a mycelium patch is. Vanilla has no
 * declarative way to co-locate two independent placed features - this
 * exists purely to guarantee that pairing, which two separate placed
 * features with matching rarity could not (each rolls its own position
 * independently). The giant mushroom can still fail to place if
 * HugeMushroomBlock's own internal space-check finds no room.
 *
 * Originally called the two sub-features directly at context.origin(), which
 * turned out to almost never actually be a valid floor spot: height_range
 * placement has no relationship to real cave shape, so the raw origin is
 * usually embedded in solid rock or floating in open air (same root cause
 * diagnosed and fixed for Cave Root Vine). A full vertical scan of the
 * origin's column fixed that, but Glenn still reported seeing none - the
 * likely remaining cause is HugeMushroomFeature's own internal space-check
 * silently declining to grow in a floor spot with a low ceiling (any random
 * air-above-solid cell might be inside a narrow tunnel with only a couple
 * blocks of headroom). Now also requires real vertical clearance above the
 * floor before accepting it, skipping past cramped spots to find a genuine
 * room for the mushroom to actually grow into.
 */
public class MushroomGroveFeature extends Feature<MushroomGroveFeature.Config> {
    // Matches this mod's established bedrock-safe Y band (see tunnels.json/soul_rift.json/etc.).
    private static final int MIN_Y = -44;
    private static final int MAX_Y = 107;
    // Giant mushrooms can grow a ~7-tall trunk plus canopy above that - require genuine
    // headroom, not just a single air-above-solid cell, before accepting a spot.
    private static final int MIN_CLEARANCE = 10;

    public MushroomGroveFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        BlockPos floor = findFloor(level, origin);
        if (floor == null) {
            return false;
        }

        Config config = context.config();
        boolean patchPlaced = Feature.VEGETATION_PATCH.place(config.patch(), level,
                context.chunkGenerator(), context.random(), floor);
        boolean mushroomPlaced = Feature.HUGE_RED_MUSHROOM.place(config.mushroom(), level,
                context.chunkGenerator(), context.random(), floor);
        return patchPlaced || mushroomPlaced;
    }

    /**
     * Full vertical scan of the origin's column for a floor with real headroom above it -
     * not just the first air-above-solid cell found, which could be a cramped tunnel.
     */
    private BlockPos findFloor(WorldGenLevel level, BlockPos origin) {
        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int y = MAX_Y; y >= MIN_Y; y--) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }
            BlockPos below = pos.below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                continue;
            }
            if (hasClearance(level, pos)) {
                return pos.immutable();
            }
        }
        return null;
    }

    private boolean hasClearance(WorldGenLevel level, BlockPos floor) {
        BlockPos.MutableBlockPos check = floor.mutable();
        for (int dy = 0; dy < MIN_CLEARANCE; dy++) {
            check.setY(floor.getY() + dy);
            if (!level.getBlockState(check).isAir()) {
                return false;
            }
        }
        return true;
    }

    public record Config(VegetationPatchConfiguration patch,
                          HugeMushroomFeatureConfiguration mushroom) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VegetationPatchConfiguration.CODEC.fieldOf("patch").forGetter(Config::patch),
                HugeMushroomFeatureConfiguration.CODEC.fieldOf("mushroom").forGetter(Config::mushroom)
        ).apply(instance, Config::new));
    }
}
