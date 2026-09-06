package com.glennfo.runicrealm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A purely decorative glowing small mushroom - deliberately simpler than
 * vanilla MushroomBlock, which requires wiring to a huge-mushroom
 * ConfiguredFeature for its bonemeal growth. That giant-mushroom feature
 * doesn't exist yet, so this skips bonemeal/growth entirely for now.
 */
public class GlowMushroomBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 6, 11);

    public GlowMushroomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP);
    }
}
