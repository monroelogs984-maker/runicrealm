package com.glennfo.runicrealm.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
 */
public class MushroomGroveFeature extends Feature<MushroomGroveFeature.Config> {
    public MushroomGroveFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        boolean patchPlaced = Feature.VEGETATION_PATCH.place(config.patch(), context.level(),
                context.chunkGenerator(), context.random(), context.origin());
        boolean mushroomPlaced = Feature.HUGE_RED_MUSHROOM.place(config.mushroom(), context.level(),
                context.chunkGenerator(), context.random(), context.origin());
        return patchPlaced || mushroomPlaced;
    }

    public record Config(VegetationPatchConfiguration patch,
                          HugeMushroomFeatureConfiguration mushroom) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VegetationPatchConfiguration.CODEC.fieldOf("patch").forGetter(Config::patch),
                HugeMushroomFeatureConfiguration.CODEC.fieldOf("mushroom").forGetter(Config::mushroom)
        ).apply(instance, Config::new));
    }
}
