package com.glennfo.runicrealm.client;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.glennfo.runicrealm.entity.RunicRealmEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
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

    // SkeletonRenderer is generic over AbstractSkeleton, so it renders our subtype with
    // vanilla's own skeleton model/texture directly - no new art needed, only the
    // equipment differs.
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RunicRealmEntities.MINER_SKELETON.get(), SkeletonRenderer::new);
    }

    private RunicRealmClient() {}
}
