package com.glennfo.runicrealm.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * Reconstructs the source portal's exact shape at the same coordinates in the
 * destination dimension (this mod's "hollow" dimension type has coordinate
 * scale 1.0, unlike the Nether's 8:1, so no coordinate scaling is needed) if
 * no portal already stands there, then lands the entity inside it. Mirrors
 * vanilla nether portals auto-building a return portal on arrival.
 *
 * getPortalInfo() is deliberately overridden (not left to Forge's default,
 * which falls back to Entity#findDimensionEntryPoint - the dimension's
 * heightmap spawn point, since "hollow" is neither Nether nor End). That
 * default position is what actually gets sent to the client in the teleport
 * packet built inside ITeleporter's own reposition callback; placeEntity()
 * runs after and only affects the server-side Entity object. A previous
 * version set the landing position via Entity#moveTo() inside placeEntity()
 * alone, which never reached the client - the client kept rendering the
 * player at the wrong (heightmap) position while the server had it standing
 * in the freshly-built portal, and the next client movement packet correcting
 * back toward its own remembered position dragged the server entity out of
 * the portal and back through it, reading as an instant kick back to the
 * origin dimension. Computing the real landing spot up front here and
 * returning it as the PortalInfo's own position fixes that at the source.
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

    private RunicPortalShape destinationShape(ServerLevel destWorld) {
        return RunicPortalShape.findFromPortalBlock(destWorld, sourceShape.bottomLeft())
                .orElseGet(() -> {
                    sourceShape.build(destWorld);
                    return sourceShape;
                });
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
                                     Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        BlockPos landing = destinationShape(destWorld).centerFloor();
        return new PortalInfo(
                new Vec3(landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5),
                entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw,
                               Function<Boolean, Entity> repositionEntity) {
        Entity moved = repositionEntity.apply(false);
        moved.resetFallDistance();
        moved.invulnerableTime = Math.max(moved.invulnerableTime, ARRIVAL_INVULNERABLE_TICKS);
        return moved;
    }
}
