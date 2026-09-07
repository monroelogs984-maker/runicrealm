package com.glennfo.runicrealm.portal;

import com.glennfo.runicrealm.block.RunicPortalBlock;
import com.glennfo.runicrealm.block.RunicRealmBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Nether-portal-style frame detection/construction, but for
 * {@link RunicRealmBlocks#RUNIC_PORTAL_CRYSTAL} frames instead of obsidian.
 * Vanilla's own PortalShape hardcodes obsidian with no way to swap the frame
 * block, so this is a standalone re-implementation - simpler than vanilla's
 * in one respect: the four corners of the rectangle must also be frame
 * blocks (vanilla nether portals don't require corners at all).
 */
public final class RunicPortalShape {
    public static final int MIN_WIDTH = 2;
    public static final int MIN_HEIGHT = 3;
    public static final int MAX_SIZE = 21;

    private final Direction.Axis axis;
    private final BlockPos bottomLeft;
    private final int width;
    private final int height;

    private RunicPortalShape(Direction.Axis axis, BlockPos bottomLeft, int width, int height) {
        this.axis = axis;
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    public Direction.Axis axis() {
        return axis;
    }

    public BlockPos bottomLeft() {
        return bottomLeft;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /** Floor-level, horizontally-centered position to land an arriving entity on. */
    public BlockPos centerFloor() {
        Direction positive = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        return bottomLeft.relative(positive, width / 2);
    }

    /** Looks for a formable portal around a just-lit soul fire block. */
    public static Optional<RunicPortalShape> findAtIgnition(LevelAccessor level, BlockPos firePos) {
        return find(level, firePos, state -> state.isAir() || state.is(RunicRealmBlocks.ETERNAL_SOUL_FIRE.get()));
    }

    /** Reconstructs the full rectangle of an existing portal from one of its interior cells. */
    public static Optional<RunicPortalShape> findFromPortalBlock(LevelAccessor level, BlockPos portalPos) {
        return find(level, portalPos, state -> state.is(RunicRealmBlocks.RUNIC_PORTAL.get()));
    }

    private static Optional<RunicPortalShape> find(LevelAccessor level, BlockPos pos, Predicate<BlockState> interior) {
        Optional<RunicPortalShape> onX = tryAxis(level, pos, Direction.Axis.X, interior);
        if (onX.isPresent()) {
            return onX;
        }
        return tryAxis(level, pos, Direction.Axis.Z, interior);
    }

    private static boolean isFrame(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos).is(RunicRealmBlocks.RUNIC_PORTAL_CRYSTAL.get());
    }

    private static Optional<RunicPortalShape> tryAxis(LevelAccessor level, BlockPos pos, Direction.Axis axis,
                                                        Predicate<BlockState> interior) {
        Direction positive = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction negative = positive.getOpposite();

        BlockPos left = pos;
        int width = 1;
        while (width <= MAX_SIZE && interior.test(level.getBlockState(left.relative(negative)))) {
            left = left.relative(negative);
            width++;
        }
        if (!isFrame(level, left.relative(negative))) {
            return Optional.empty();
        }

        BlockPos right = pos;
        while (width <= MAX_SIZE && interior.test(level.getBlockState(right.relative(positive)))) {
            right = right.relative(positive);
            width++;
        }
        if (width < MIN_WIDTH || width > MAX_SIZE || !isFrame(level, right.relative(positive))) {
            return Optional.empty();
        }

        BlockPos bottom = pos;
        int height = 1;
        while (height <= MAX_SIZE && interior.test(level.getBlockState(bottom.below()))) {
            bottom = bottom.below();
            height++;
        }
        if (!isFrame(level, bottom.below())) {
            return Optional.empty();
        }

        BlockPos top = pos;
        while (height <= MAX_SIZE && interior.test(level.getBlockState(top.above()))) {
            top = top.above();
            height++;
        }
        if (height < MIN_HEIGHT || height > MAX_SIZE || !isFrame(level, top.above())) {
            return Optional.empty();
        }

        BlockPos bottomLeft = new BlockPos(left.getX(), bottom.getY(), left.getZ());

        for (int w = -1; w <= width; w++) {
            for (int h = -1; h <= height; h++) {
                BlockPos cell = bottomLeft.relative(positive, w).above(h);
                boolean edge = w == -1 || w == width || h == -1 || h == height;
                if (edge) {
                    if (!isFrame(level, cell)) {
                        return Optional.empty();
                    }
                } else if (!interior.test(level.getBlockState(cell))) {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(new RunicPortalShape(axis, bottomLeft, width, height));
    }

    /** Fills the interior with runic portal blocks, matching this shape's axis. */
    public void fill(LevelAccessor level) {
        Direction positive = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        BlockState portalState = RunicRealmBlocks.RUNIC_PORTAL.get().defaultBlockState()
                .setValue(RunicPortalBlock.AXIS, axis);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                level.setBlock(bottomLeft.relative(positive, w).above(h), portalState, 18);
            }
        }
    }

    /**
     * Builds the full frame (including corners) plus interior at this shape's
     * coordinates, first clearing a pocket of open space on both sides so the
     * portal generates exposed rather than embedded in solid rock - mirrors
     * vanilla nether portals carving space when linking to a fresh location.
     */
    public void build(LevelAccessor level) {
        clearSurroundings(level);
        Direction positive = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        BlockState frameState = RunicRealmBlocks.RUNIC_PORTAL_CRYSTAL.get().defaultBlockState();
        for (int w = -1; w <= width; w++) {
            for (int h = -1; h <= height; h++) {
                boolean edge = w == -1 || w == width || h == -1 || h == height;
                if (edge) {
                    level.setBlock(bottomLeft.relative(positive, w).above(h), frameState, 18);
                }
            }
        }
        fill(level);
    }

    // 1 block was not enough to reliably feel "open" against arbitrary cave terrain -
    // carve a real room: 2 blocks deep on both sides of the plane, with a 2-block
    // margin around the frame's footprint too, instead of hugging its exact outline.
    private static final int CLEAR_DEPTH = 2;
    private static final int CLEAR_MARGIN = 2;

    private void clearSurroundings(LevelAccessor level) {
        Direction positive = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction depthDir = axis == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int w = -1 - CLEAR_MARGIN; w <= width + CLEAR_MARGIN; w++) {
            for (int h = -1 - CLEAR_MARGIN; h <= height + CLEAR_MARGIN; h++) {
                BlockPos plane = bottomLeft.relative(positive, w).above(h);
                for (int d = 1; d <= CLEAR_DEPTH; d++) {
                    level.setBlock(plane.relative(depthDir, d), air, 18);
                    level.setBlock(plane.relative(depthDir.getOpposite(), d), air, 18);
                }
            }
        }
    }
}
