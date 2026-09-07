package com.glennfo.runicrealm.client;

import com.glennfo.runicrealm.entity.Firefly;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Deliberately draws nothing - EntityRenderer's base render() is a no-op
 * unless a subclass adds model geometry, so not overriding it here means
 * Firefly has no visible model at all. The only thing you actually see is
 * the minecraft:light block it drops at its own position each tick (see
 * Firefly.updateLight()) - a soft glow drifting through the dark, which is
 * the whole point, no bug sprite needed for that to read as "firefly."
 * getTextureLocation() still needs a real answer (used for things like the
 * F3 debug entity list), so it points at an existing vanilla texture that's
 * never actually drawn.
 */
public class FireflyRenderer extends EntityRenderer<Firefly> {
    private static final ResourceLocation PLACEHOLDER_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/bat.png");

    public FireflyRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(Firefly entity) {
        return PLACEHOLDER_TEXTURE;
    }
}
