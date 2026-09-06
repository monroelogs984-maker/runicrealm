package com.glennfo.runicrealm.client;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class RunicRealmClient {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(RunicRealmBlocks.ETERNAL_SOUL_FIRE.get(), RenderType.cutout());
            // Cross-model mushrooms need cutout too, same as vanilla's own small mushrooms/
            // flowers - otherwise the transparent parts of the sprite render solid.
            RunicRealmBlocks.GLOW_MUSHROOMS.values()
                    .forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
        });
    }

    private RunicRealmClient() {}
}
