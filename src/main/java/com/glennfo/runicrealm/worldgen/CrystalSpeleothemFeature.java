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
 * A straight vertical column of crystal blocks - a stalagmite rising from a
 * floor or a stalactite hanging from a ceiling (picked randomly per call).
 * Column height is fully config-driven: 1 for the common "small" variant
 * (a single crystal nub), 10-15 for the rare "large" variant (each block
 * drops itself, so total yield falls naturally out of column height).
 * Material is picked randomly per formation between Glimmering Crystal
 * (white) and Dark Glimmering Crystal (dark blue), per Glenn's ask.
 *
 * First version searched a small +-10 vertical / +-4 horizontal window
 * around one random height_range Y pick and never found anything - the
 * exact same flaw already diagnosed and fixed for Cave Root Vine and the
 * mushroom grove (height_range has no relationship to real cave shape, and
 * a small local window usually doesn't touch open space at all over this
 * dimension's ~150-block Y range). Fixed the same way: a full vertical
 * scan of the origin's column across the mod's whole bedrock-safe Y band.
 */
public class CrystalSpeleothemFeature extends Feature<CrystalSpeleothemFeature.Config> {
    // Matches this mod's established bedrock-safe Y band (see tunnels.json/soul_rift.json/etc.).
    private static final int MIN_Y = -44;
    private static final int MAX_Y = 107;

    public CrystalSpeleothemFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int height = context.config().height().sample(random);
        boolean fromFloor = random.nextBoolean();
        Direction support = fromFloor ? Direction.DOWN : Direction.UP;
        Direction grow = fromFloor ? Direction.UP : Direction.DOWN;
        BlockState crystal = (random.nextBoolean()
                ? RunicRealmBlocks.GLIMMERING_CRYSTAL.get()
                : RunicRealmBlocks.DARK_GLIMMERING_CRYSTAL.get()).defaultBlockState();

        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int y = MAX_Y; y >= MIN_Y; y--) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }
            BlockPos supportPos = pos.relative(support);
            if (!level.getBlockState(supportPos).isFaceSturdy(level, supportPos, support.getOpposite())) {
                continue;
            }
            BlockPos start = pos.immutable();
            if (hasClearance(level, start, grow, height)) {
                placeColumn(level, start, grow, height, crystal);
                return true;
            }
        }
        return false;
    }

    private boolean hasClearance(WorldGenLevel level, BlockPos start, Direction grow, int height) {
        for (int i = 0; i < height; i++) {
            if (!level.getBlockState(start.relative(grow, i)).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void placeColumn(WorldGenLevel level, BlockPos start, Direction grow, int height, BlockState crystal) {
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
