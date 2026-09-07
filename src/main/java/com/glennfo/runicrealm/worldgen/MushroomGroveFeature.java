package com.glennfo.runicrealm.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

/**
 * Places a vegetation_patch (mycelium + scattered small mushrooms) and a
 * huge_red_mushroom - the actual giant mushroom cap/stem blocks - in the
 * same worldgen pass, so a giant mushroom is attempted every time a
 * mycelium patch is. Vanilla has no declarative way to co-locate two
 * independent placed features - this exists purely to guarantee that
 * pairing, which two separate placed features with matching rarity could
 * not (each rolls its own position independently).
 *
 * These are two visually distinct things that got conflated across a few
 * rounds of fixes: the patch is just ground texture + small decorative
 * mushrooms, cheap to place almost anywhere with a floor; the giant mushroom
 * is the actual multi-block cap/stem structure built from
 * glow_mushroom_block_<color>/glow_mushroom_stem, which needs real room to
 * grow into and was the thing still missing. An earlier fix required 10
 * blocks of vertical clearance before accepting ANY floor at all - which
 * did make giant mushrooms more likely where it succeeded, but also
 * suppressed the patch itself everywhere that didn't have a tall room,
 * i.e. most of this dimension's narrower tunnels. Patch and giant mushroom
 * now search independently: the patch uses a lenient floor search (any
 * sturdy footing, same as originally), and the giant mushroom uses its own
 * stricter search requiring both vertical clearance and a bit of horizontal
 * room around where the canopy would sit (foliage_radius 3).
 *
 * The actual reason the giant mushroom kept failing regardless of room,
 * found by reading HugeRedMushroomFeature/AbstractHugeMushroomFeature's
 * real isValidPosition() via javap: it requires the block directly below
 * the origin to be dirt OR tagged #minecraft:mushroom_grow_block - not
 * just "sturdy," a specific floor-type whitelist. This dimension's terrain
 * is plain stone, which is neither, so every attempt failed at that check
 * before space was even considered. Fixed two ways: added our 16
 * glowing_mycelium_<color> blocks to that tag (data/minecraft/tags/blocks/
 * mushroom_grow_block.json), and explicitly place the matching mycelium
 * ground state directly under the giant mushroom's own chosen floor before
 * calling HUGE_RED_MUSHROOM - its search runs independently of the patch's,
 * so it can't just hope the patch happened to convert the same spot.
 */
public class MushroomGroveFeature extends Feature<MushroomGroveFeature.Config> {
    // Matches this mod's established bedrock-safe Y band (see tunnels.json/soul_rift.json/etc.).
    private static final int MIN_Y = -44;
    private static final int MAX_Y = 107;
    // Giant mushrooms can grow a ~7-tall trunk plus canopy above that.
    private static final int MUSHROOM_CLEARANCE = 8;
    private static final int CANOPY_RADIUS = 3;

    public MushroomGroveFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        Config config = context.config();

        BlockPos patchFloor = findFloor(level, origin);
        boolean patchPlaced = patchFloor != null && Feature.VEGETATION_PATCH.place(config.patch(), level,
                context.chunkGenerator(), context.random(), patchFloor);

        BlockPos mushroomFloor = findMushroomFloor(level, origin);
        boolean mushroomPlaced = false;
        if (mushroomFloor != null) {
            RandomSource random = context.random();
            // HugeRedMushroomFeature.isValidPosition() requires the floor to be dirt or
            // #minecraft:mushroom_grow_block (verified via javap) - place our own mycelium
            // there directly rather than hoping the patch's independent footprint covers it.
            level.setBlock(mushroomFloor.below(), config.patch().groundState.getState(random, mushroomFloor.below()), 3);
            mushroomPlaced = Feature.HUGE_RED_MUSHROOM.place(config.mushroom(), level,
                    context.chunkGenerator(), random, mushroomFloor);
        }

        return patchPlaced || mushroomPlaced;
    }

    /** Lenient full-column scan: the first air cell with sturdy footing below it. */
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

    /** Stricter scan: a floor with a real room above it - vertical AND horizontal clearance. */
    private BlockPos findMushroomFloor(WorldGenLevel level, BlockPos origin) {
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
            if (hasRoom(level, pos.immutable())) {
                return pos.immutable();
            }
        }
        return null;
    }

    private boolean hasRoom(WorldGenLevel level, BlockPos floor) {
        for (int dy = 0; dy < MUSHROOM_CLEARANCE; dy++) {
            if (!level.getBlockState(floor.above(dy)).isAir()) {
                return false;
            }
        }
        BlockPos canopyCenter = floor.above(MUSHROOM_CLEARANCE - 1);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(canopyCenter.relative(direction, CANOPY_RADIUS)).isAir()) {
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
