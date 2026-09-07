package com.glennfo.runicrealm.worldgen;

import com.glennfo.runicrealm.RunicRealm;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RunicRealmFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, RunicRealm.MODID);

    public static final RegistryObject<Feature<MushroomGroveFeature.Config>> MUSHROOM_GROVE =
            FEATURES.register("mushroom_grove", () -> new MushroomGroveFeature(MushroomGroveFeature.Config.CODEC));

    public static final RegistryObject<Feature<SoulFireClumpFeature.Config>> SOUL_FIRE_CLUMP =
            FEATURES.register("soul_fire_clump", () -> new SoulFireClumpFeature(SoulFireClumpFeature.Config.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CAVE_ROOT =
            FEATURES.register("cave_root", () -> new CaveRootFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<CrystalSpeleothemFeature.Config>> CRYSTAL_SPELEOTHEM =
            FEATURES.register("crystal_speleothem",
                    () -> new CrystalSpeleothemFeature(CrystalSpeleothemFeature.Config.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SHRINE =
            FEATURES.register("shrine", () -> new ShrineFeature(NoneFeatureConfiguration.CODEC));

    private RunicRealmFeatures() {}
}
