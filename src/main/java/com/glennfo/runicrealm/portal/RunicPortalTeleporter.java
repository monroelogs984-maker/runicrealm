package com.glennfo.runicrealm.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * Reconstructs the source portal's exact shape at the same coordinates in the
 * destination dimension (this mod's "hollow" dimension type has coordinate
 * scale 1.0, unlike the Nether's 8:1, so no coordinate scaling is needed) if
 * no portal already stands there, then lands the entity inside it. Mirrors
 * vanilla nether portals auto-building a return portal on arrival.
 */
public class RunicPortalTeleporter implements ITeleporter {
    // Grace period on arrival: fall distance reset (you might land at a different
    // Y in the destination) plus a brief damage-immunity window, same idea as
    // vanilla's spawn invulnerability.
    private static final int ARRIVAL_INVULNERABLE_TICKS = 20;

    private final RunicPortalShape sourceShape;

    public RunicPortalTeleporter(RunicPortalShape sourceShape) {
        this.sourceShape = sourceShape;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw,
                               Function<Boolean, Entity> repositionEntity) {
        Entity moved = repositionEntity.apply(false);

        RunicPortalShape destShape = RunicPortalShape.findFromPortalBlock(destWorld, sourceShape.bottomLeft())
                .orElseGet(() -> {
                    sourceShape.build(destWorld);
                    return sourceShape;
                });

        BlockPos landing = destShape.centerFloor();
        moved.moveTo(landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5, moved.getYRot(), moved.getXRot());
        moved.resetFallDistance();
        moved.invulnerableTime = Math.max(moved.invulnerableTime, ARRIVAL_INVULNERABLE_TICKS);
        return moved;
    }
}
