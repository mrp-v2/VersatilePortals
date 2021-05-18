package mrp_v2.versatileportals.world;

import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.ITeleporter;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public class BasicWorldTeleporter implements ITeleporter
{
    protected final ServerWorld destinationWorld;
    protected final ServerWorld originWorld;
    protected final PortalSize originPortalSize;

    public BasicWorldTeleporter(ServerWorld destinationWorld, ServerWorld originWorld, PortalSize originPortalSize)
    {
        this.destinationWorld = destinationWorld;
        this.originWorld = originWorld;
        this.originPortalSize = originPortalSize;
    }

    @Nullable @Override
    public Entity placeEntity(Entity entityIn, ServerWorld currentWorld, ServerWorld destinationWorld, float yaw,
            Function<Boolean, Entity> repositionEntity)
    {
        Entity repositionedEntity = repositionEntity.apply(false); // don't let vanilla make a portal
        IPortalDataCapability portalData = Util.getPortalData(repositionedEntity);
        portalData.setRemainingPortalCooldown(repositionedEntity.getPortalCooldown());
        portalData.setInPortalTime(0);
        return repositionedEntity;
    }

    @Nullable @Override public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld,
            Function<ServerWorld, PortalInfo> defaultPortalInfo)
    {
        return this.getPortalInfo(entity);
    }

    @Nullable private PortalInfo getPortalInfo(Entity entity)
    {
        WorldBorder worldborder = this.destinationWorld.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
        double coordinateMultiplier = DimensionType
                .getCoordinateDifference(entity.world.getDimensionType(), this.destinationWorld.getDimensionType());
        BlockPos pos =
                new BlockPos(MathHelper.clamp(entity.getPosX() * coordinateMultiplier, minX, maxX), entity.getPosY(),
                        MathHelper.clamp(entity.getPosZ() * coordinateMultiplier, minZ, maxZ));
        return this.getExistingPortal(pos, coordinateMultiplier)
                .map((result) -> this.convertTeleportResult(result, entity)).orElseGet(
                        () -> this.makePortal(pos).map((result) -> this.convertTeleportResult(result, entity))
                                .orElse(null));
    }

    private PortalInfo convertTeleportResult(TeleportationRepositioner.Result result, Entity entity)
    {
        Direction.Axis axis = this.originPortalSize.getAxis();
        Vector3d vector3d = new Vector3d(0.5D, 0.0D, 0.0D);
        return func_242963_a(this.destinationWorld, result, axis, vector3d, entity.getSize(entity.getPose()),
                entity.getMotion(), entity.rotationYaw, entity.rotationPitch);
    }

    private static PortalInfo func_242963_a(ServerWorld world, TeleportationRepositioner.Result result,
            Direction.Axis portalAxis, Vector3d offsetVector, EntitySize entitySize, Vector3d motion, float rotationYaw,
            float rotationPitch)
    {
        BlockPos portalBlockPos = result.startPos;
        BlockState portalBlockState = world.getBlockState(portalBlockPos);
        Direction.Axis portalBlockAxis = portalBlockState.get(BlockStateProperties.AXIS);
        double portalSizeA = result.width;
        double portalSizeB = result.height;
        int yawAdjustment;
        Vector3d adjustedMotionVector;
        if (portalAxis != portalBlockAxis)
        {
            if (portalAxis == Direction.Axis.Y || portalBlockAxis == Direction.Axis.Y)
            {
                yawAdjustment = 0;
                if (portalAxis == Direction.Axis.X || portalBlockAxis == Direction.Axis.X)
                {
                    adjustedMotionVector = new Vector3d(motion.y, motion.z, motion.x);
                } else
                {
                    adjustedMotionVector = new Vector3d(motion.z, motion.x, motion.y);
                }
            } else
            {
                yawAdjustment = 90;
                adjustedMotionVector = new Vector3d(motion.z, motion.y, -motion.x);
            }
        } else
        {
            yawAdjustment = 0;
            adjustedMotionVector = motion;
        }
        Vector3d adjustedPositionVector;
        double portalCenterA = entitySize.width / 2.0D + (portalSizeA - entitySize.width) * offsetVector.getX();
        double portalCenterAB = entitySize.width / 2.0D + (portalSizeB - entitySize.width) * offsetVector.getX();
        double portalCenterB = (portalSizeB - entitySize.height) * offsetVector.getY();
        double portalCenterC = 0.5D + offsetVector.getZ();
        switch (portalBlockAxis)
        {
            case X:
                adjustedPositionVector =
                        new Vector3d(portalBlockPos.getX() + portalCenterC, portalBlockPos.getY() + portalCenterB,
                                portalBlockPos.getZ() + portalCenterA);
                break;
            case Y:
                adjustedPositionVector =
                        new Vector3d(portalBlockPos.getX() + portalCenterA, portalBlockPos.getY() + portalCenterB,
                                portalBlockPos.getZ() + portalCenterAB);
                break;
            case Z:
                adjustedPositionVector =
                        new Vector3d(portalBlockPos.getX() + portalCenterA, portalBlockPos.getY() + portalCenterB,
                                portalBlockPos.getZ() + portalCenterC);
                break;
            default:
                throw new IllegalStateException();
        }
        return new PortalInfo(adjustedPositionVector, adjustedMotionVector, rotationYaw + yawAdjustment, rotationPitch);
    }

    public Optional<TeleportationRepositioner.Result> getExistingPortal(BlockPos searchOrigin,
            double coordinateMultiplier)
    {
        PointOfInterestManager pointOfInterestManager = this.destinationWorld.getPointOfInterestManager();
        int i = coordinateMultiplier == 0.125D ? 16 : 128;
        pointOfInterestManager.ensureLoadedAndValid(this.destinationWorld, searchOrigin, i);
        Optional<PointOfInterest> optionalPointOfInterest = pointOfInterestManager.getInSquare(
                (pointOfInterestType) -> pointOfInterestType == ObjectHolder.PORTAL_POINT_OF_INTEREST_TYPE.get(),
                searchOrigin, i, PointOfInterestManager.Status.ANY).sorted(Comparator.<PointOfInterest>comparingDouble(
                (pointOfInterest) -> pointOfInterest.getPos().distanceSq(searchOrigin))
                .thenComparingInt((pointOfInterest) -> pointOfInterest.getPos().getY()))
                .filter((pointOfInterest) -> this.destinationWorld.getBlockState(pointOfInterest.getPos())
                        .hasProperty(BlockStateProperties.AXIS)).findFirst();
        return optionalPointOfInterest.map((pointOfInterest) ->
        {
            BlockPos poiPos = pointOfInterest.getPos();
            this.destinationWorld.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(poiPos), 3, poiPos);
            BlockState poiState = this.destinationWorld.getBlockState(poiPos);
            Pair<Direction.Axis, Direction.Axis> otherAxes =
                    Util.OTHER_AXES_MAP.get(poiState.get(BlockStateProperties.AXIS));
            // finds the destination portal size
            return TeleportationRepositioner
                    .findLargestRectangle(poiPos, otherAxes.getLeft(), PortalSize.MAX_SIZE, otherAxes.getRight(),
                            PortalSize.MAX_SIZE,
                            (lambdaPos) -> this.destinationWorld.getBlockState(lambdaPos) == poiState);
        });
    }

    public Optional<TeleportationRepositioner.Result> makePortal(BlockPos pos)
    {
        double availablePortalLocDistance = -1.0D;
        BlockPos availablePortalLoc = null;
        double partiallyAvailablePortalLocDistance = -1.0D;
        BlockPos partiallyAvailablePortalLoc = null;
        WorldBorder worldBorder = this.destinationWorld.getWorldBorder();
        int maxPortalGenerationHeight = this.destinationWorld.func_234938_ad_() - 1;
        BlockPos.Mutable mutableOriginPos = pos.toMutable();
        for (BlockPos.Mutable testPos : BlockPos.func_243514_a(pos, 16, Direction.EAST, Direction.SOUTH))
        {
            int maxPortalGenerationHeightForPos = Math.min(maxPortalGenerationHeight,
                    this.destinationWorld.getHeight(Heightmap.Type.MOTION_BLOCKING, testPos.getX(), testPos.getZ()));
            if (worldBorder.contains(testPos) && worldBorder
                    .contains(testPos.move(this.originPortalSize.getDirA()).move(this.originPortalSize.getDirB())))
            {
                testPos.move(this.originPortalSize.getOppositeDirA()).move(this.originPortalSize.getOppositeDirB());
                for (int availableYSpaceBottom = maxPortalGenerationHeightForPos; availableYSpaceBottom >= 0;
                     availableYSpaceBottom--)
                {
                    testPos.setY(availableYSpaceBottom);
                    if (this.destinationWorld.isAirBlock(testPos))
                    {
                        int availableYSpaceTop;
                        for (availableYSpaceTop = availableYSpaceBottom; availableYSpaceBottom > 0 &&
                                this.destinationWorld.isAirBlock(testPos.move(Direction.DOWN)); availableYSpaceBottom--)
                        {
                        }
                        if (availableYSpaceBottom + this.originPortalSize.getSizeOnAxis(Direction.Axis.Y) + 1 <=
                                maxPortalGenerationHeight)
                        {
                            int availableYSpace = availableYSpaceTop - availableYSpaceBottom;
                            if (availableYSpace <= 0 || availableYSpace >= this.originPortalSize.getSizeA() + 1)
                            {
                                testPos.setY(availableYSpaceBottom);
                                if (this.checkRegionForPlacement(testPos, mutableOriginPos, Direction
                                        .getFacingFromAxisDirection(this.originPortalSize.getAxis(),
                                                Direction.AxisDirection.POSITIVE), 0))
                                {
                                    double distance = pos.distanceSq(testPos);
                                    if (this.checkRegionForPlacement(testPos, mutableOriginPos, Direction
                                            .getFacingFromAxisDirection(this.originPortalSize.getAxis(),
                                                    Direction.AxisDirection.POSITIVE), -1) &&
                                            this.checkRegionForPlacement(testPos, mutableOriginPos, Direction
                                                    .getFacingFromAxisDirection(this.originPortalSize.getAxis(),
                                                            Direction.AxisDirection.POSITIVE), 1) &&
                                            (availablePortalLocDistance == -1.0D ||
                                                    availablePortalLocDistance > distance))
                                    {
                                        availablePortalLocDistance = distance;
                                        availablePortalLoc = testPos.toImmutable();
                                    }
                                    if (availablePortalLocDistance == -1.0D &&
                                            (partiallyAvailablePortalLocDistance == -1.0D ||
                                                    partiallyAvailablePortalLocDistance > distance))
                                    {
                                        partiallyAvailablePortalLocDistance = distance;
                                        partiallyAvailablePortalLoc = testPos.toImmutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (availablePortalLocDistance == -1.0D && partiallyAvailablePortalLocDistance != -1.0D)
        {
            availablePortalLoc = partiallyAvailablePortalLoc;
            availablePortalLocDistance = partiallyAvailablePortalLocDistance;
        }
        final int floorWidth = 1;
        if (availablePortalLocDistance == -1.0D)
        {
            availablePortalLoc = (new BlockPos(pos.getX(),
                    MathHelper.clamp(pos.getY(), 70, this.destinationWorld.func_234938_ad_() - 10), pos.getZ()))
                    .toImmutable();
            Direction openPortalDir = Direction
                    .getFacingFromAxisDirection(this.originPortalSize.getAxis(), Direction.AxisDirection.POSITIVE);
            if (!worldBorder.contains(availablePortalLoc))
            {
                return Optional.empty();
            }
            BlockState airState = Blocks.AIR.getDefaultState();
            if (this.originPortalSize.getAxis() != Direction.Axis.Y)
            {
                for (int floorXZ = -floorWidth; floorXZ <= floorWidth;
                     floorXZ++) // clears an area and creates the floor
                {
                    for (int portalXZ = 0; portalXZ < this.originPortalSize.getHorizontalSize(); portalXZ++)
                    {
                        for (int y = -1; y < this.originPortalSize.getVerticalSize(); y++)
                        {
                            BlockState blockState =
                                    y < 0 ? ObjectHolder.PORTAL_FRAME_BLOCK.get().getDefaultState() : airState;
                            mutableOriginPos.setAndOffset(availablePortalLoc,
                                    portalXZ * this.originPortalSize.getDirA().getXOffset() +
                                            floorXZ * openPortalDir.getXOffset(), y,
                                    portalXZ * this.originPortalSize.getDirA().getZOffset() +
                                            floorXZ * openPortalDir.getZOffset());
                            this.destinationWorld.setBlockState(mutableOriginPos, blockState);
                        }
                    }
                }
            } else
            {
                final int verticalAirSpace = 2;
                for (int y = -verticalAirSpace; y <= verticalAirSpace; y++)
                {
                    for (int x = 0; x < this.originPortalSize.getSizeOnAxis(Direction.Axis.X); x++)
                    {
                        for (int z = 0; z < this.originPortalSize.getSizeOnAxis(Direction.Axis.Z); z++)
                        {
                            mutableOriginPos.setAndOffset(availablePortalLoc, x, y, z);
                            this.destinationWorld.setBlockState(mutableOriginPos, airState);
                        }
                    }
                }
            }
        }
        int portalSizeA = this.originPortalSize.getSizeA();
        int portalSizeB = this.originPortalSize.getSizeB();
        for (int dirAOffset = -1; dirAOffset < portalSizeA + 1; dirAOffset++)
        {
            for (int dirBOffset = -1; dirBOffset < portalSizeB + 1; dirBOffset++)
            {
                if (dirAOffset == -1 | dirAOffset == portalSizeA || dirBOffset == -1 || dirBOffset == portalSizeB)
                {
                    Vector3i offsetVector = new Vector3i(dirAOffset * this.originPortalSize.getDirA().getXOffset() +
                            dirBOffset * this.originPortalSize.getDirB().getXOffset(),
                            dirAOffset * this.originPortalSize.getDirA().getYOffset() +
                                    dirBOffset * this.originPortalSize.getDirB().getYOffset(),
                            dirAOffset * this.originPortalSize.getDirA().getZOffset() +
                                    dirBOffset * this.originPortalSize.getDirB().getZOffset());
                    mutableOriginPos.setAndOffset(availablePortalLoc, offsetVector.getX(), offsetVector.getY(),
                            offsetVector.getZ());
                    if (this.originPortalSize.getPortalControllerRelativePos().equals(new BlockPos(offsetVector)))
                    {
                        this.destinationWorld.setBlockState(mutableOriginPos,
                                ObjectHolder.PORTAL_CONTROLLER_BLOCK.get().getDefaultState()
                                        .with(PortalControllerBlock.AXIS, this.originPortalSize.getAxis()), 3);
                        PortalControllerTileEntity portalControllerTileEntity =
                                (PortalControllerTileEntity) this.destinationWorld.getTileEntity(mutableOriginPos);
                        portalControllerTileEntity.getInventory()
                                .insertItem(0, ExistingWorldControlItem.getItemForWorld(this.originWorld), false);
                        portalControllerTileEntity.setPortalColor(
                                this.originPortalSize.getPortalController(this.originWorld).getLeft().getPortalColor());
                    } else
                    {
                        this.destinationWorld.setBlockState(mutableOriginPos,
                                ObjectHolder.PORTAL_FRAME_BLOCK.get().getDefaultState(), 3);
                    }
                }
            }
        }
        BlockState portalBlockState = ObjectHolder.PORTAL_BLOCK.get().getDefaultState()
                .with(BlockStateProperties.AXIS, this.originPortalSize.getAxis());
        for (int dirAOffset = 0; dirAOffset < portalSizeA; dirAOffset++) // creates portal
        {
            for (int dirBOffset = 0; dirBOffset < portalSizeB; dirBOffset++)
            {
                Vector3i offsetVector = new Vector3i(dirAOffset * this.originPortalSize.getDirA().getXOffset() +
                        dirBOffset * this.originPortalSize.getDirB().getXOffset(),
                        dirAOffset * this.originPortalSize.getDirA().getYOffset() +
                                dirBOffset * this.originPortalSize.getDirB().getYOffset(),
                        dirAOffset * this.originPortalSize.getDirA().getZOffset() +
                                dirBOffset * this.originPortalSize.getDirB().getZOffset());
                mutableOriginPos.setAndOffset(availablePortalLoc, offsetVector.getX(), offsetVector.getY(),
                        offsetVector.getZ());
                this.destinationWorld.setBlockState(mutableOriginPos, portalBlockState, 18);
            }
        }
        return Optional
                .of(new TeleportationRepositioner.Result(availablePortalLoc.toImmutable(), portalSizeA, portalSizeB));
    }

    private boolean checkRegionForPlacement(BlockPos originalPos, BlockPos.Mutable offsetPos, Direction axis,
            int offsetScale)
    {
        Vector3i combinedOffsetVector = new Vector3i(
                this.originPortalSize.getDirA().getXOffset() + this.originPortalSize.getDirB().getXOffset(),
                this.originPortalSize.getDirA().getYOffset() + this.originPortalSize.getDirB().getYOffset(),
                this.originPortalSize.getDirA().getZOffset() + this.originPortalSize.getDirB().getZOffset());
        for (int i = -1; i < this.originPortalSize.getSizeA(); i++)
        {
            for (int j = -1; j < this.originPortalSize.getSizeB(); j++)
            {
                offsetPos.setAndOffset(originalPos, combinedOffsetVector.getX() * i + axis.getXOffset() * offsetScale,
                        combinedOffsetVector.getY() * j + axis.getYOffset() * offsetScale,
                        combinedOffsetVector.getZ() * i + axis.getZOffset() * offsetScale);
                if (j < 0 && !this.destinationWorld.getBlockState(offsetPos).getMaterial().isSolid())
                {
                    return false;
                }
                if (j >= 0 && !this.destinationWorld.isAirBlock(offsetPos))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
