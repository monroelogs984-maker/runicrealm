package com.glennfo.runicrealm.block;

import com.glennfo.runicrealm.dimension.RunicRealmDimensions;
import com.glennfo.runicrealm.portal.RunicPortalShape;
import com.glennfo.runicrealm.portal.RunicPortalTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import java.util.Optional;

/**
 * The runic equivalent of NetherPortalBlock. Not extended from it directly -
 * vanilla's own portal travel (Entity#handleNetherPortal) is hardcoded to the
 * Nether/Overworld pair, so entity teleport here is handled independently via
 * Forge's ITeleporter instead of Entity#handleInsidePortal.
 */
public class RunicPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final VoxelShape X_AXIS_AABB = Block.box(0, 0, 6, 16, 16, 10);
    private static final VoxelShape Z_AXIS_AABB = Block.box(6, 0, 0, 10, 16, 16);
    private static final String PORTAL_TIME_KEY = "RunicRealmPortalTime";

    public RunicPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction side = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        return isPortalOrFrame(level, pos.relative(side)) && isPortalOrFrame(level, pos.relative(side.getOpposite()))
                && isPortalOrFrame(level, pos.above()) && isPortalOrFrame(level, pos.below());
    }

    private boolean isPortalOrFrame(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(this) || state.is(RunicRealmBlocks.RUNIC_PORTAL_CRYSTAL.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F,
                    random.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int i = 0; i < 4; ++i) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double dx = (random.nextDouble() - 0.5) * 0.5;
            double dy = (random.nextDouble() - 0.5) * 0.5;
            double dz = (random.nextDouble() - 0.5) * 0.5;
            int side = random.nextInt(2) * 2 - 1;
            if (level.getBlockState(pos.west()).is(this) || level.getBlockState(pos.east()).is(this)) {
                x = pos.getX() + 0.5 + 0.25 * side;
                dx = random.nextDouble() * 2.0 * side;
            } else {
                z = pos.getZ() + 0.5 + 0.25 * side;
                dz = random.nextDouble() * 2.0 * side;
            }
            level.addParticle(ParticleTypes.PORTAL, x, y, z, dx, dy, dz);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!entity.canChangeDimensions() || entity.isPassenger() || !entity.isAlive() || entity.isOnPortalCooldown()) {
            return;
        }

        // getPortalWaitTime() is vanilla's own standing-delay hook: 80 ticks for a
        // survival player (Player overrides it), 1 tick in creative, 0 (instant) for
        // the base Entity - matches real Nether portal timing instead of a guess.
        CompoundTag data = entity.getPersistentData();
        int time = data.getInt(PORTAL_TIME_KEY) + 1;
        if (time < entity.getPortalWaitTime()) {
            data.putInt(PORTAL_TIME_KEY, time);
            return;
        }
        data.putInt(PORTAL_TIME_KEY, 0);

        Optional<RunicPortalShape> shape = RunicPortalShape.findFromPortalBlock(level, pos);
        if (shape.isEmpty()) {
            return;
        }

        MinecraftServer server = serverLevel.getServer();
        ResourceKey<Level> targetDimension = serverLevel.dimension() == RunicRealmDimensions.HOLLOW
                ? Level.OVERWORLD : RunicRealmDimensions.HOLLOW;
        ServerLevel targetLevel = server.getLevel(targetDimension);
        if (targetLevel == null || targetLevel == serverLevel) {
            return;
        }

        entity.setPortalCooldown();
        entity.changeDimension(targetLevel, new RunicPortalTeleporter(shape.get()));
    }
}
