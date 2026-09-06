package com.glennfo.runicrealm.block;

import com.glennfo.runicrealm.RunicRealm;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RunicRealmBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RunicRealm.MODID);

    // Properties mirror vanilla Blocks.SOUL_FIRE exactly (verified via javap against
    // the official-mapped 1.20.1 jar) - only canSurvive/onPlace/tick differ.
    public static final RegistryObject<Block> ETERNAL_SOUL_FIRE = BLOCKS.register("eternal_soul_fire",
            () -> new EternalSoulFireBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 10)
                    .sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY)));

    private RunicRealmBlocks() {}
}
