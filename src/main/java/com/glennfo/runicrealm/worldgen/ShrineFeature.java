package com.glennfo.runicrealm.worldgen;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.glennfo.runicrealm.entity.RunicRealmEntities;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A rare landmark room: a Runic Portal Crystal platform, a giant Runic
 * Crystal Cluster column at the center, a loot chest, and a spawner
 * guarding it with a Miner Skeleton - a mini dungeon, not just passive
 * loot, per Glenn's ask. Not a real vanilla Structure (no StructureSet/
 * jigsaw/NBT template) - a programmatic room built directly by this
 * Feature, same general approach as every other custom worldgen feature
 * in this mod. That means none of vanilla's structure tooling (locate
 * command, structure block export/import, biome-based structure sets)
 * applies to it; it's a placed_feature like the rest.
 */
public class ShrineFeature extends Feature<NoneFeatureConfiguration> {
    // Matches this mod's established bedrock-safe Y band (see tunnels.json/soul_rift.json/etc.).
    private static final int MIN_Y = -44;
    private static final int MAX_Y = 107;
    private static final int ROOM_RADIUS = 6;
    private static final int ROOM_HEIGHT = 14;
    private static final int PLATFORM_RADIUS = 4;
    private static final int CRYSTAL_HEIGHT = 12;
    private static final ResourceLocation CHEST_LOOT_TABLE =
            new ResourceLocation("runicrealm", "chests/shrine");

    public ShrineFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos floor = findRoom(level, context.origin());
        if (floor == null) {
            return false;
        }

        buildPlatform(level, floor);
        buildCrystal(level, floor.above());
        buildChest(level, random, floor.above().offset(PLATFORM_RADIUS - 1, 0, 0));
        buildSpawner(level, random, floor.above().offset(-(PLATFORM_RADIUS - 1), 0, 0));
        return true;
    }

    /** Full-column scan for a floor with a genuinely large room above it - vertical and horizontal. */
    private BlockPos findRoom(WorldGenLevel level, BlockPos origin) {
        BlockPos.MutableBlockPos pos = origin.mutable();
        for (int y = MAX_Y; y >= MIN_Y; y--) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) {
                continue;
            }
            BlockPos below = pos.below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                continue;
            }
            if (hasRoom(level, pos.immutable())) {
                return pos.immutable();
            }
        }
        return null;
    }

    private boolean hasRoom(WorldGenLevel level, BlockPos floor) {
        for (int dy = 0; dy < ROOM_HEIGHT; dy++) {
            BlockPos center = floor.above(dy);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (!level.getBlockState(center.relative(direction, ROOM_RADIUS)).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void buildPlatform(WorldGenLevel level, BlockPos floor) {
        BlockState crystal = RunicRealmBlocks.RUNIC_PORTAL_CRYSTAL.get().defaultBlockState();
        int radiusSq = PLATFORM_RADIUS * PLATFORM_RADIUS;
        for (int x = -PLATFORM_RADIUS; x <= PLATFORM_RADIUS; x++) {
            for (int z = -PLATFORM_RADIUS; z <= PLATFORM_RADIUS; z++) {
                if (x * x + z * z <= radiusSq) {
                    level.setBlock(floor.offset(x, 0, z), crystal, 3);
                }
            }
        }
    }

    private void buildCrystal(WorldGenLevel level, BlockPos base) {
        BlockState crystal = RunicRealmBlocks.RUNIC_CRYSTAL_CLUSTER.get().defaultBlockState();
        for (int i = 0; i < CRYSTAL_HEIGHT; i++) {
            level.setBlock(base.above(i), crystal, 3);
        }
    }

    private void buildChest(WorldGenLevel level, RandomSource random, BlockPos pos) {
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RandomizableContainerBlockEntity) {
            RandomizableContainerBlockEntity.setLootTable(level, random, pos, CHEST_LOOT_TABLE);
        }
    }

    private void buildSpawner(WorldGenLevel level, RandomSource random, BlockPos pos) {
        level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 3);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SpawnerBlockEntity spawner) {
            spawner.setEntityId(RunicRealmEntities.MINER_SKELETON.get(), random);
        }
    }
}
