package com.glennfo.runicrealm.item;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
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
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Acts like vanilla flint and steel (campfire/candle lighting is untouched),
 * but always lights {@link RunicRealmBlocks#ETERNAL_SOUL_FIRE} - soul fire
 * that survives anywhere, not just soul sand/soil - when igniting open ground.
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
        if (!level.getBlockState(targetPos).isAir()) {
            return InteractionResult.FAIL;
        }
        BlockState soulFireState = RunicRealmBlocks.ETERNAL_SOUL_FIRE.get().defaultBlockState();

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
