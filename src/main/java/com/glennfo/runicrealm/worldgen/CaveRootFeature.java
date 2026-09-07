package com.glennfo.runicrealm.worldgen;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A direct port of vanilla's own VinesFeature (verified via javap - it's
 * genuinely this simple), just placing our Cave Root Vine instead of
 * Blocks.VINE. Earlier versions tried to be clever - scanning a whole
 * column, picking "the best" spot, growing a deliberate multi-block strip
 * at placement time - and none of it worked reliably. Vanilla's real
 * approach is the opposite: test the origin directly with zero searching,
 * check all 5 non-DOWN directions, place a single seed block at the first
 * one that has a wall/ceiling, and do nothing otherwise. The "spread about"
 * jungle-vine look isn't built at placement time at all - it comes from
 * VineBlock's own randomTick() naturally growing each seed into a longer
 * strand over real game time (inherited automatically since
 * CAVE_ROOT_VINE is a real VineBlock with randomTicks() enabled), combined
 * with a large placement count (see placed_feature/cave_root.json) so
 * enough seeds land near a wall purely by volume of cheap attempts, the
 * same way vanilla's own vines.json uses count: 127 per chunk instead of
 * trying to be smart about where it looks.
 */
public class CaveRootFeature extends Feature<NoneFeatureConfiguration> {
    public CaveRootFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        if (!level.isEmptyBlock(origin)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) {
                continue;
            }
            if (VineBlock.isAcceptableNeighbour(level, origin.relative(direction), direction)) {
                BlockState vineState = RunicRealmBlocks.CAVE_ROOT_VINE.get().defaultBlockState()
                        .setValue(VineBlock.getPropertyForFace(direction), true);
                level.setBlock(origin, vineState, 2);
                return true;
            }
        }
        return false;
    }
}
