package com.glennfo.runicrealm.block;

import com.glennfo.runicrealm.portal.RunicPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Soul fire that can be lit on any block, not just soul sand/soil. On a real
 * soul-fire base it behaves exactly like vanilla soul fire (permanent). On
 * anything else it survives placement (vanilla SoulFireBlock.canSurvive would
 * normally have BaseFireBlock#onPlace remove it in the same tick) but burns
 * out on its own shortly after via a scheduled tick, instead of persisting
 * forever - unless lighting it completes a Runic Portal Crystal frame, in
 * which case it's consumed into the portal instead of scheduling a burnout.
 */
public class EternalSoulFireBlock extends SoulFireBlock {
    private static final int BURN_OUT_TICKS = 60;

    public EternalSoulFireBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (oldState.is(state.getBlock())) {
            return;
        }

        if (!level.isClientSide()) {
            Optional<RunicPortalShape> shape = RunicPortalShape.findAtIgnition(level, pos);
            if (shape.isPresent()) {
                shape.get().fill(level);
                return;
            }
        }

        if (!canSurviveOnBlock(level.getBlockState(pos.below()))) {
            level.scheduleTick(pos, this, BURN_OUT_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurviveOnBlock(level.getBlockState(pos.below()))) {
            level.removeBlock(pos, false);
        }
    }
}
