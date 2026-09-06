package com.glennfo.runicrealm.client;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class RunicRealmClient {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                ItemBlockRenderTypes.setRenderLayer(RunicRealmBlocks.ETERNAL_SOUL_FIRE.get(), RenderType.cutout()));
    }

    private RunicRealmClient() {}
}
