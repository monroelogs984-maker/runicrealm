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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * A single vertical strip of Cave Root vines clinging to one wall face,
 * picked once per placement (not vanilla VineBlock's own multi-face spread,
 * which would grow into a 2D blob over time rather than a clean strip).
 * Reuses VineBlock directly - same attachment/property logic as the real
 * vanilla vines block, just placed deliberately here instead of spreading
 * randomly. isAcceptableNeighbour() is vanilla's own validity check
 * (verified via javap, including the exact (level, pos.relative(face),
 * face) argument order canSurvive itself uses), so every placed cell is
 * guaranteed to survive its own canSurvive check afterward.
 */
public class CaveRootFeature extends Feature<CaveRootFeature.Config> {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public CaveRootFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int length = context.config().length().sample(random);

        Direction face = HORIZONTAL[random.nextInt(HORIZONTAL.length)];
        BooleanProperty property = VineBlock.getPropertyForFace(face);
        var vineState = RunicRealmBlocks.CAVE_ROOT_VINE.get().defaultBlockState().setValue(property, true);

        boolean placedAny = false;
        BlockPos.MutableBlockPos pos = origin.mutable();
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
