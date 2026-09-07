package com.glennfo.runicrealm.worldgen;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * A single vertical strip of Cave Root vines clinging to one wall face.
 * Reuses VineBlock directly - same attachment/property logic as the real
 * vanilla vines block - just placed deliberately as a straight strip instead
 * of relying on VineBlock's own multi-face spread (which grows into a 2D
 * blob over time, not a clean strip).
 *
 * Second version sampled a neighborhood of random candidates around the
 * origin instead of testing only the exact origin, still wasn't reliable
 * enough - height_range placement has no relationship to real cave shape, so
 * a small local search box around one random Y often doesn't touch open
 * space at all. Third version switched to a full vertical column scan
 * (still here), fixing the "never even near a cave" problem, but Glenn still
 * reported seeing none - so this pass also widens what counts as a valid
 * attachment: ceilings (UP) now count alongside the 4 walls, roughly
 * doubling how many found air cells actually have somewhere to attach (a
 * cell deep in a large pocket may have no adjacent wall in any horizontal
 * direction but still have solid rock directly overhead).
 */
public class CaveRootFeature extends Feature<CaveRootFeature.Config> {
    private static final Direction[] ATTACH_DIRECTIONS =
            {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};
    // Matches this mod's established bedrock-safe Y band (see tunnels.json/soul_rift.json/etc.).
    private static final int MIN_Y = -44;
    private static final int MAX_Y = 107;

    public CaveRootFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int length = context.config().length().sample(random);

        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int y = MAX_Y; y >= MIN_Y; y--) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }
            Direction face = pickValidFace(level, pos, random);
            if (face == null) {
                continue;
            }
            if (placeStrip(level, pos.immutable(), face, length)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prefers a wall (proper long strip) when one exists; only falls back to the
     * ceiling (UP - a real attachment, but placeStrip below will cut it short at
     * 1 block since the vine itself isn't solid) for cells with no wall at all,
     * which would otherwise fail outright - e.g. a cell deep inside a large pocket.
     */
    private Direction pickValidFace(WorldGenLevel level, BlockPos pos, RandomSource random) {
        List<Direction> validWalls = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            Direction direction = ATTACH_DIRECTIONS[i];
            if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction)) {
                validWalls.add(direction);
            }
        }
        if (!validWalls.isEmpty()) {
            return validWalls.get(random.nextInt(validWalls.size()));
        }
        return VineBlock.isAcceptableNeighbour(level, pos.above(), Direction.UP) ? Direction.UP : null;
    }

    private boolean placeStrip(WorldGenLevel level, BlockPos start, Direction face, int length) {
        BooleanProperty property = VineBlock.getPropertyForFace(face);
        BlockState vineState = RunicRealmBlocks.CAVE_ROOT_VINE.get().defaultBlockState().setValue(property, true);

        boolean placedAny = false;
        BlockPos.MutableBlockPos pos = start.mutable();
        for (int i = 0; i < length; i++) {
            if (!level.getBlockState(pos).isAir()) {
                break;
            }
            if (!VineBlock.isAcceptableNeighbour(level, pos.relative(face), face)) {
                break;
            }
            level.setBlock(pos, vineState, 3);
            placedAny = true;
            pos.move(Direction.DOWN);
        }
        return placedAny;
    }

    public record Config(IntProvider length) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IntProvider.CODEC.fieldOf("length").forGetter(Config::length)
        ).apply(instance, Config::new));
    }
}
