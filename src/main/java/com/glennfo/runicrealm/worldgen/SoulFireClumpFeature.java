package com.glennfo.runicrealm.worldgen;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Scatters a small clump of Eternal Soul Fire around the origin, each flame
 * on its own patch of soul soil so it burns forever (matches
 * EternalSoulFireBlock's own on-a-real-base rule - a worldgen decoration
 * placed on plain stone would burn out again within seconds otherwise).
 * Vanilla's own patch_soul_fire only checks for pre-existing soul soil
 * (Soul Sand Valley's terrain already is soul soil); this dimension's floor
 * is plain stone, so the base has to be placed here too, not just assumed.
 */
public class SoulFireClumpFeature extends Feature<SoulFireClumpFeature.Config> {
    private static final int RADIUS = 4;
    private static final int VERTICAL_SEARCH = 6;

    public SoulFireClumpFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int count = context.config().count().sample(random);

        boolean placedAny = false;
        for (int i = 0; i < count; i++) {
            int dx = random.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dz = random.nextInt(RADIUS * 2 + 1) - RADIUS;
            BlockPos.MutableBlockPos pos = origin.offset(dx, 0, dz).mutable();

            BlockPos floor = findFloor(level, pos);
            if (floor == null) {
                continue;
            }

            level.setBlock(floor, Blocks.SOUL_SOIL.defaultBlockState(), 3);
            level.setBlock(floor.above(), RunicRealmBlocks.ETERNAL_SOUL_FIRE.get().defaultBlockState(), 3);
            placedAny = true;
        }
        return placedAny;
    }

    /** Searches near pos.y for the first air cell with solid, sturdy footing beneath it. */
    private BlockPos findFloor(WorldGenLevel level, BlockPos.MutableBlockPos pos) {
        for (int dy = -VERTICAL_SEARCH; dy <= VERTICAL_SEARCH; dy++) {
            BlockPos candidate = pos.atY(pos.getY() + dy);
            BlockState here = level.getBlockState(candidate);
            if (!here.isAir()) {
                continue;
            }
            BlockPos below = candidate.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.isFaceSturdy(level, below, Direction.UP)) {
                return candidate;
            }
        }
        return null;
    }

    public record Config(IntProvider count) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IntProvider.CODEC.fieldOf("count").forGetter(Config::count)
        ).apply(instance, Config::new));
    }
}
