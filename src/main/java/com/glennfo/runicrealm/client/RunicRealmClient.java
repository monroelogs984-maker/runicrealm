package com.glennfo.runicrealm.client;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.glennfo.runicrealm.entity.RunicRealmEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
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
            // Same bug class again: the vine texture's transparent silhouette needs cutout
            // or it renders as an opaque square, exactly like soul fire/mushrooms before it.
            ItemBlockRenderTypes.setRenderLayer(RunicRealmBlocks.CAVE_ROOT_VINE.get(), RenderType.cutout());
        });
    }

    // SkeletonRenderer is generic over AbstractSkeleton, so it renders our subtype with
    // vanilla's own skeleton model/texture directly - no new art needed, only the
    // equipment differs.
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RunicRealmEntities.MINER_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(RunicRealmEntities.FIREFLY.get(), FireflyRenderer::new);
        // Each new hostile extends the exact vanilla class its renderer is generic over, so
        // vanilla's own model/texture render it directly - no new art, only equipment/stats
        // differ. Visual placeholder until real crystal/corrupted-themed art exists.
        event.registerEntityRenderer(RunicRealmEntities.SPELEOTHEM_GUARDIAN.get(), IronGolemRenderer::new);
        event.registerEntityRenderer(RunicRealmEntities.RIFT_STALKER.get(), ZombieRenderer::new);
        event.registerEntityRenderer(RunicRealmEntities.CORRUPTED_WISP.get(), VexRenderer::new);
        event.registerEntityRenderer(RunicRealmEntities.CORRUPTED_ARMOR.get(), ZombieRenderer::new);
        event.registerEntityRenderer(RunicRealmEntities.POOL_LURKER.get(), SilverfishRenderer::new);
    }

    private RunicRealmClient() {}
}
