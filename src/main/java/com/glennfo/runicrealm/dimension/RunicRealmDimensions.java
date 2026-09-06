package com.glennfo.runicrealm.dimension;

import com.glennfo.runicrealm.RunicRealm;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class RunicRealmDimensions {
    public static final ResourceKey<Level> HOLLOW = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(RunicRealm.MODID, "hollow"));

    private RunicRealmDimensions() {}
}
