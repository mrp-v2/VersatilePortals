package mrp_v2.randomdimensions.world;

import mrp_v2.randomdimensions.block.PortalSize;
import mrp_v2.randomdimensions.common.capabilities.IPortalDataCapability;
import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public class Teleporter implements ITeleporter
{
    protected final ServerWorld world;
    protected final Direction.Axis axis;

    public Teleporter(ServerWorld worldIn, Direction.Axis axis)
    {
        this.world = worldIn;
        this.axis = axis;
    }

    @Override
    public Entity placeEntity(Entity entityIn, ServerWorld currentWorld, ServerWorld destinationWorld, float yaw,
            Function<Boolean, Entity> repositionEntity)
    {
        PortalInfo portalInfo = getPortalInfo(entityIn);
        if (portalInfo == null)
        {
            return null;
        }
        Entity returnEntity;
        if (entityIn instanceof PlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) entityIn;
            currentWorld.getProfiler().startSection("moving");
            currentWorld.getProfiler().endSection();
            currentWorld.getProfiler().startSection("placing");
            player.setWorld(destinationWorld);
            destinationWorld.addDuringPortalTeleport(player);
            player.rotationYaw = portalInfo.field_242960_c;
            player.rotationPitch = portalInfo.field_242961_d;
            player.setPositionAndUpdate(portalInfo.pos.x, portalInfo.pos.y, portalInfo.pos.z);
            player.connection.captureCurrentPosition();
            currentWorld.getProfiler().endSection();
            returnEntity = player;
        } else
        {
            this.world.getProfiler().endStartSection("reloading");
            Entity newEntity = entityIn.getType().create(destinationWorld);
            if (newEntity != null)
            {
                newEntity.copyDataFromOld(entityIn);
                newEntity.setLocationAndAngles(portalInfo.pos.x, portalInfo.pos.y, portalInfo.pos.z,
                        portalInfo.field_242960_c, entityIn.rotationPitch);
                newEntity.setMotion(portalInfo.motion);
                destinationWorld.addFromAnotherDimension(newEntity);
            }
            returnEntity = newEntity;
        }
        IPortalDataCapability portalData = Util.getPortalData(returnEntity);
        portalData.setRemainingPortalCooldown(returnEntity.getPortalCooldown());
        portalData.setInPortalTime(0);
        returnEntity.getMaxInPortalTime();
        return returnEntity;
    }

    @Nullable private PortalInfo getPortalInfo(Entity entity)
    {
        WorldBorder worldborder = this.world.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
        double coordinateMultiplier =
                DimensionType.func_242715_a(entity.world.func_230315_m_(), this.world.func_230315_m_());
        BlockPos pos =
                new BlockPos(MathHelper.clamp(entity.getPosX() * coordinateMultiplier, minX, maxX), entity.getPosY(),
                        MathHelper.clamp(entity.getPosZ() * coordinateMultiplier, minZ, maxZ));
        return this.teleportExistingPortal(pos, coordinateMultiplier)
                .map((result) -> this.convertTeleportResult(result, entity))
                .orElseGet(() -> this.teleportNewPortal(pos)
                        .map((result) -> this.convertTeleportResult(result, entity))
                        .orElse(null));
    }

    private PortalInfo convertTeleportResult(TeleportationRepositioner.Result result, Entity entity)
    {
        Direction.Axis axis = Direction.Axis.X;
        Vector3d vector3d = new Vector3d(0.5D, 0.0D, 0.0D);
        return net.minecraft.block.PortalSize.func_242963_a(this.world, result, axis, vector3d,
                entity.getSize(entity.getPose()), entity.getMotion(), entity.rotationYaw, entity.rotationPitch);
    }

    public Optional<TeleportationRepositioner.Result> teleportExistingPortal(BlockPos searchOrigin,
            double coordinateMultiplier)
    {
        PointOfInterestManager pointOfInterestManager = this.world.getPointOfInterestManager();
        int i = coordinateMultiplier == 0.125D ? 16 : 128;
        pointOfInterestManager.ensureLoadedAndValid(this.world, searchOrigin, i);
        Optional<PointOfInterest> optionalPointOfInterest = pointOfInterestManager.getInSquare(
                (pointOfInterestType) -> pointOfInterestType == ObjectHolder.PORTAL_POINT_OF_INTEREST_TYPE,
                searchOrigin, i, PointOfInterestManager.Status.ANY)
                .sorted(Comparator.<PointOfInterest>comparingDouble(
                        (pointOfInterest) -> pointOfInterest.getPos().distanceSq(searchOrigin)).thenComparingInt(
                        (pointOfInterest) -> pointOfInterest.getPos().getY()))
                .filter((pointOfInterest) -> this.world.getBlockState(pointOfInterest.getPos())
                        .hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                .findFirst();
        return optionalPointOfInterest.map((pointOfInterest) ->
        {
            BlockPos blockPos = pointOfInterest.getPos();
            this.world.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(blockPos), 3, blockPos);
            BlockState blockState1 = this.world.getBlockState(blockPos);
            return TeleportationRepositioner.func_243676_a(blockPos,
                    blockState1.get(BlockStateProperties.HORIZONTAL_AXIS), PortalSize.MAX_WIDTH, Direction.Axis.Y, 21,
                    (blockState2) -> this.world.getBlockState(blockState2) == blockState1);
        });
    }

    public Optional<TeleportationRepositioner.Result> teleportNewPortal(BlockPos pos)
    {
        Direction direction = Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, this.axis);
        double d0 = -1.0D;
        BlockPos blockPos1 = null;
        double d1 = -1.0D;
        BlockPos blockPos2 = null;
        WorldBorder worldBorder = this.world.getWorldBorder();
        int i = this.world.func_234938_ad_() - 1;
        BlockPos.Mutable mutableBlockPos1 = pos.toMutable();
        for (BlockPos.Mutable mutableBlockPos2 : BlockPos.func_243514_a(pos, 16, Direction.EAST, Direction.SOUTH))
        {
            int j = Math.min(i, this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, mutableBlockPos2.getX(),
                    mutableBlockPos2.getZ()));
            if (worldBorder.contains(mutableBlockPos2) && worldBorder.contains(mutableBlockPos2.move(direction, 1)))
            {
                mutableBlockPos2.move(direction.getOpposite(), 1);
                for (int l = j; l >= 0; l--)
                {
                    mutableBlockPos2.setY(l);
                    if (this.world.isAirBlock(mutableBlockPos2))
                    {
                        int i1;
                        for (i1 = l; l > 0 && this.world.isAirBlock(mutableBlockPos2.move(Direction.DOWN)); l--)
                        {
                        }
                        if (l + 4 <= i)
                        {
                            int j1 = i1 - l;
                            if (j1 <= 0 || j1 >= 3)
                            {
                                mutableBlockPos2.setY(l);
                                if (this.isSpaceEmpty(mutableBlockPos2, mutableBlockPos1, direction, 0))
                                {
                                    double d2 = pos.distanceSq(mutableBlockPos2);
                                    if (this.isSpaceEmpty(mutableBlockPos2, mutableBlockPos1, direction, -1) &&
                                            this.isSpaceEmpty(mutableBlockPos2, mutableBlockPos1, direction, 1) &&
                                            (d0 == -1.0D || d0 > d2))
                                    {
                                        d0 = d2;
                                        blockPos1 = mutableBlockPos2.toImmutable();
                                    }
                                    if (d0 == -1.0D && (d1 == -1.0D || d1 > d2))
                                    {
                                        d1 = d2;
                                        blockPos2 = mutableBlockPos2.toImmutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (d0 == -1.0D && d1 != -1.0D)
        {
            blockPos1 = blockPos2;
            d0 = d1;
        }
        int floorWidth = 2;
        int portalWidth = 2;
        int portalHeight = 3;
        if (d0 == -1.0D)
        {
            blockPos1 = (new BlockPos(pos.getX(), MathHelper.clamp(pos.getY(), 70, this.world.func_234938_ad_() - 10),
                    pos.getZ())).toImmutable();
            Direction rotated = direction.rotateY();
            if (!worldBorder.contains(blockPos1))
            {
                return Optional.empty();
            }
            for (int l1 = -1; l1 < floorWidth; l1++) // creates floor of generated portal and air above
            {
                for (int k2 = 0; k2 < portalWidth; k2++)
                {
                    for (int i3 = -1; i3 < portalHeight; i3++)
                    {
                        BlockState blockState = i3 < 0 ?
                                ObjectHolder.INDESTRUCTIBLE_PORTAL_FRAME_BLOCK.getDefaultState() :
                                Blocks.AIR.getDefaultState();
                        mutableBlockPos1.func_239621_a_(blockPos1,
                                k2 * direction.getXOffset() + l1 * rotated.getXOffset(), i3,
                                k2 * direction.getZOffset() + l1 * rotated.getZOffset());
                        this.world.setBlockState(mutableBlockPos1, blockState);
                    }
                }
            }
        }
        for (int k1 = -1; k1 < portalWidth + 1; k1++) // creates portal frame
        {
            for (int i2 = -1; i2 < portalHeight + 1; i2++)
            {
                if (k1 == -1 | k1 == portalWidth || i2 == -1 || i2 == portalHeight)
                {
                    mutableBlockPos1.func_239621_a_(blockPos1, k1 * direction.getXOffset(), i2,
                            k1 * direction.getZOffset());
                    this.world.setBlockState(mutableBlockPos1,
                            ObjectHolder.INDESTRUCTIBLE_PORTAL_FRAME_BLOCK.getDefaultState(), 3);
                }
            }
        }
        BlockState blockState = ObjectHolder.INDESTRUCTIBLE_PORTAL_BLOCK.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_AXIS, this.axis);
        for (int j2 = 0; j2 < portalWidth; j2++) // creates portal
        {
            for (int l2 = 0; l2 < portalHeight; l2++)
            {
                mutableBlockPos1.func_239621_a_(blockPos1, j2 * direction.getXOffset(), l2,
                        j2 * direction.getZOffset());
                this.world.setBlockState(mutableBlockPos1, blockState, 18);
            }
        }
        return Optional.of(new TeleportationRepositioner.Result(blockPos1.toImmutable(), portalWidth, portalHeight));
    }

    private boolean isSpaceEmpty(BlockPos origin, BlockPos.Mutable mutable, Direction axis, int offset)
    {
        Direction rotated = axis.rotateY();
        for (int i = -1; i < PortalSize.MIN_WIDTH + 1; i++)
        {
            for (int j = -1; j < PortalSize.MIN_HEIGHT + 1; j++)
            {
                mutable.func_239621_a_(origin, axis.getXOffset() * i + rotated.getXOffset() * offset, j,
                        axis.getZOffset() * i + rotated.getZOffset() * offset);
                if (j < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid())
                {
                    return false;
                }
                if (j >= 0 && !this.world.isAirBlock(mutable))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
