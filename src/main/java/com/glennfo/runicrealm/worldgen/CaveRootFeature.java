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
 * First version only ever tested the placement's exact origin position with
 * one fixed random direction, which essentially never lands on a wall -
 * height_range placement has no relationship to actual cave shape, so the
 * origin is usually embedded in solid rock or floating in open air. Fixed by
 * sampling a neighborhood of candidate positions around the origin (like
 * vanilla's own cave-decoration features do internally) until a genuine
 * air-cell-next-to-a-wall spot is found.
 */
public class CaveRootFeature extends Feature<CaveRootFeature.Config> {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int ATTEMPTS = 32;
    private static final int HORIZONTAL_RADIUS = 5;
    private static final int VERTICAL_RADIUS = 6;

    public CaveRootFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int length = context.config().length().sample(random);

        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            int dx = random.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
            int dy = random.nextInt(VERTICAL_RADIUS * 2 + 1) - VERTICAL_RADIUS;
            int dz = random.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
            BlockPos candidate = origin.offset(dx, dy, dz);

            if (!level.getBlockState(candidate).isAir()) {
                continue;
            }
            Direction face = pickValidFace(level, candidate, random);
            if (face == null) {
                continue;
            }
            if (placeStrip(level, candidate, face, length)) {
                return true;
            }
        }
        return false;
    }

    /** Checks all 4 horizontal directions and picks randomly among the ones with a real wall. */
    private Direction pickValidFace(WorldGenLevel level, BlockPos pos, RandomSource random) {
        List<Direction> valid = new ArrayList<>(4);
        for (Direction direction : HORIZONTAL) {
            if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction)) {
                valid.add(direction);
            }
        }
        return valid.isEmpty() ? null : valid.get(random.nextInt(valid.size()));
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
