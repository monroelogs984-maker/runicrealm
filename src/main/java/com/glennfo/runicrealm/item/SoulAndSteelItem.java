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
 * but always forces soul fire when igniting open ground, regardless of what
 * block sits below the target position. Vanilla flint and steel already
 * places soul fire automatically over soul sand/soil ({@code BaseFireBlock.getState}) -
 * this item exists so a future portal frame can key off soul fire specifically
 * and ignore regular flint and steel.
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
