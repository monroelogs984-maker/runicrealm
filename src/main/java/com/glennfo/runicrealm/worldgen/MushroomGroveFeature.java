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
 * diagnosed and fixed for Cave Root Vine). Now does its own floor search
 * first - a full vertical scan of the origin's column - and hands the
 * sub-features an actual floor position instead of a blind guess.
 */
public class MushroomGroveFeature extends Feature<MushroomGroveFeature.Config> {
    // Matches this mod's established bedrock-safe Y band (see tunnels.json/soul_rift.json/etc.).
    private static final int MIN_Y = -44;
    private static final int MAX_Y = 107;

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

    /** Full vertical scan of the origin's column for the first air cell with sturdy footing. */
    private BlockPos findFloor(WorldGenLevel level, BlockPos origin) {
        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int y = MAX_Y; y >= MIN_Y; y--) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }
            BlockPos below = pos.below();
            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                return pos.immutable();
            }
        }
        return null;
    }

    public record Config(VegetationPatchConfiguration patch,
                          HugeMushroomFeatureConfiguration mushroom) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VegetationPatchConfiguration.CODEC.fieldOf("patch").forGetter(Config::patch),
                HugeMushroomFeatureConfiguration.CODEC.fieldOf("mushroom").forGetter(Config::mushroom)
        ).apply(instance, Config::new));
    }
}
