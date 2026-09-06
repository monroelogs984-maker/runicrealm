package com.glennfo.runicrealm.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Acts like vanilla flint and steel (campfire/candle lighting is untouched),
 * but always forces plain vanilla soul fire ({@link Blocks#SOUL_FIRE}) when
 * igniting open ground, regardless of what's below the target position.
 * It's the same soul fire block placing it on soul sand/soil normally would
 * give you - it just won't survive anywhere else, same as always.
 */
public class SoulAndSteelItem extends FlintAndSteelItem {
    public SoulAndSteelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (CampfireBlock.canLight(clickedState) || CandleBlock.canLight(clickedState) || CandleCakeBlock.canLight(clickedState)) {
            return super.useOn(context);
        }

        BlockPos targetPos = clickedPos.relative(context.getClickedFace());
        BlockState soulFireState = Blocks.SOUL_FIRE.defaultBlockState();
        if (!level.getBlockState(targetPos).isAir() || !soulFireState.canSurvive(level, targetPos)) {
            return InteractionResult.FAIL;
        }

        level.playSound(player, targetPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.setBlock(targetPos, soulFireState, 11);
        level.gameEvent(player, GameEvent.BLOCK_PLACE, targetPos);

        ItemStack stack = context.getItemInHand();
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, targetPos, stack);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
