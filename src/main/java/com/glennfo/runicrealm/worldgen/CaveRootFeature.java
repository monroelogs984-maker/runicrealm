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
 * origin instead of testing only the exact origin, but that still wasn't
 * reliable enough - Glenn reported still seeing none. Root problem: over the
 * huge Y range this placement can land in (height_range spans ~150 blocks),
 * caves/tunnels/pockets are a small fraction of the total volume, so a
 * modest local search box around one random Y often doesn't touch open space
 * at all, not just "misses the exact wall." Fixed properly this time: a full
 * vertical scan of the origin's (x,z) column across the mod's whole
 * bedrock-safe Y band, same fix already applied to MushroomGroveFeature for
 * the identical underlying reason. If that column touches a cave anywhere in
 * the band, this finds it.
 */
public class CaveRootFeature extends Feature<CaveRootFeature.Config> {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
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
