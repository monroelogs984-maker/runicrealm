package com.glennfo.runicrealm.worldgen;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * A straight vertical column of Runic Crystal Cluster blocks - a stalagmite
 * rising from a floor or a stalactite hanging from a ceiling (picked
 * randomly per attempt, matching Glenn's "stalactites/stalagmites" ask).
 * Column height is fully config-driven: 1 for the common "small" variant
 * (a single crystal nub, drops 1 crystal), 10-15 for the rare "large"
 * variant (drops 10-15 crystals total, one per block broken - the total
 * yield falls naturally out of the column height rather than needing a
 * separate drop-count mechanic).
 */
public class CrystalSpeleothemFeature extends Feature<CrystalSpeleothemFeature.Config> {
    private static final int ATTEMPTS = 16;
    private static final int RADIUS = 4;
    private static final int VERTICAL_SEARCH = 10;

    public CrystalSpeleothemFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int height = context.config().height().sample(random);

        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            int dx = random.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dz = random.nextInt(RADIUS * 2 + 1) - RADIUS;
            BlockPos column = origin.offset(dx, 0, dz);

            boolean fromFloor = random.nextBoolean();
            BlockPos start = findAnchor(level, column, fromFloor);
            if (start == null) {
                continue;
            }
            Direction grow = fromFloor ? Direction.UP : Direction.DOWN;
            if (!hasClearance(level, start, grow, height)) {
                continue;
            }
            placeColumn(level, start, grow, height);
            return true;
        }
        return false;
    }

    /** Searches near column.y for an air cell resting on a floor (fromFloor) or under a ceiling. */
    private BlockPos findAnchor(WorldGenLevel level, BlockPos column, boolean fromFloor) {
        Direction support = fromFloor ? Direction.DOWN : Direction.UP;
        BlockPos.MutableBlockPos pos = column.mutable();
        for (int dy = -VERTICAL_SEARCH; dy <= VERTICAL_SEARCH; dy++) {
            pos.setY(column.getY() + dy);
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }
            BlockPos supportPos = pos.relative(support);
            if (level.getBlockState(supportPos).isFaceSturdy(level, supportPos, support.getOpposite())) {
                return pos.immutable();
            }
        }
        return null;
    }

    private boolean hasClearance(WorldGenLevel level, BlockPos start, Direction grow, int height) {
        BlockPos.MutableBlockPos check = start.mutable();
        for (int i = 0; i < height; i++) {
            check.set(start.relative(grow, i));
            if (!level.getBlockState(check).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void placeColumn(WorldGenLevel level, BlockPos start, Direction grow, int height) {
        BlockState crystal = RunicRealmBlocks.RUNIC_CRYSTAL_CLUSTER.get().defaultBlockState();
        for (int i = 0; i < height; i++) {
            level.setBlock(start.relative(grow, i), crystal, 3);
        }
    }

    public record Config(IntProvider height) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IntProvider.CODEC.fieldOf("height").forGetter(Config::height)
        ).apply(instance, Config::new));
    }
}
