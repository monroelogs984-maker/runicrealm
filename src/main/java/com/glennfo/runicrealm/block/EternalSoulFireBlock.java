package com.glennfo.runicrealm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Soul fire that skips the soul sand/soil base check vanilla SoulFireBlock
 * enforces in canSurvive - lit by Soul and Steel, it burns forever on
 * anything, not just soul sand/soil.
 */
public class EternalSoulFireBlock extends SoulFireBlock {
    public EternalSoulFireBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }
}
