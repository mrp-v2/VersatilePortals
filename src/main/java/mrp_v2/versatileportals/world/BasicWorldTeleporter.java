package mrp_v2.versatileportals.world;

import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public class BasicWorldTeleporter implements ITeleporter {
    protected final ServerLevel destinationWorld;
    protected final ServerLevel originWorld;
    protected final PortalSize originPortalSize;

    public BasicWorldTeleporter(ServerLevel destinationWorld, ServerLevel originWorld, PortalSize originPortalSize) {
        this.destinationWorld = destinationWorld;
        this.originWorld = originWorld;
        this.originPortalSize = originPortalSize;
    }

    private static PortalInfo createPortalInfo(ServerLevel world, BlockUtil.FoundRectangle result,
                                               Direction.Axis portalAxis, Vec3 offsetVector, EntityDimensions entitySize, Vec3 motion, float rotationYaw,
                                               float rotationPitch) {
        BlockPos portalBlockPos = result.minCorner;
        BlockState portalBlockState = world.getBlockState(portalBlockPos);
        Direction.Axis portalBlockAxis = portalBlockState.getValue(BlockStateProperties.AXIS);
        double portalSizeA = result.axis1Size;
        double portalSizeB = result.axis2Size;
        int yawAdjustment;
        Vec3 adjustedMotionVector;
        if (portalAxis != portalBlockAxis) {
            if (portalAxis == Direction.Axis.Y || portalBlockAxis == Direction.Axis.Y) {
                yawAdjustment = 0;
                if (portalAxis == Direction.Axis.X || portalBlockAxis == Direction.Axis.X) {
                    adjustedMotionVector = new Vec3(motion.y, motion.z, motion.x);
                } else {
                    adjustedMotionVector = new Vec3(motion.z, motion.x, motion.y);
                }
            } else {
                yawAdjustment = 90;
                adjustedMotionVector = new Vec3(motion.z, motion.y, -motion.x);
            }
        } else {
            yawAdjustment = 0;
            adjustedMotionVector = motion;
        }
        Vec3 adjustedPositionVector;
        double portalCenterA = entitySize.width / 2.0D + (portalSizeA - entitySize.width) * offsetVector.x();
        double portalCenterAB = entitySize.width / 2.0D + (portalSizeB - entitySize.width) * offsetVector.x();
        double portalCenterB = (portalSizeB - entitySize.height) * offsetVector.y();
        double portalCenterC = 0.5D + offsetVector.z();
        adjustedPositionVector = switch (portalBlockAxis) {
            case X -> new Vec3(portalBlockPos.getX() + portalCenterC, portalBlockPos.getY() + portalCenterB,
                    portalBlockPos.getZ() + portalCenterA);
            case Y -> new Vec3(portalBlockPos.getX() + portalCenterA, portalBlockPos.getY() + portalCenterB,
                    portalBlockPos.getZ() + portalCenterAB);
            case Z -> new Vec3(portalBlockPos.getX() + portalCenterA, portalBlockPos.getY() + portalCenterB,
                    portalBlockPos.getZ() + portalCenterC);
            default -> throw new IllegalStateException();
        };
        return new PortalInfo(adjustedPositionVector, adjustedMotionVector, rotationYaw + yawAdjustment, rotationPitch);
    }

    @Nullable
    @Override
    public Entity placeEntity(Entity entityIn, ServerLevel currentWorld, ServerLevel destinationWorld, float yaw,
                              Function<Boolean, Entity> repositionEntity) {
        Entity repositionedEntity = repositionEntity.apply(false); // don't let vanilla make a portal
        IPortalDataCapability portalData = Util.getPortalData(repositionedEntity);
        portalData.setRemainingPortalCooldown(repositionedEntity.getDimensionChangingDelay());
        portalData.setInPortalTime(0);
        return repositionedEntity;
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
                                    Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return this.getPortalInfo(entity);
    }

    @Nullable
    private PortalInfo getPortalInfo(Entity entity) {
        WorldBorder worldborder = this.destinationWorld.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, worldborder.getMinX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, worldborder.getMinZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, worldborder.getMaxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, worldborder.getMaxZ() - 16.0D);
        double coordinateMultiplier = DimensionType
                .getTeleportationScale(entity.level.dimensionType(), this.destinationWorld.dimensionType());
        BlockPos pos = new BlockPos(Mth.clamp(entity.getX() * coordinateMultiplier, minX, maxX), entity.getY(),
                Mth.clamp(entity.getZ() * coordinateMultiplier, minZ, maxZ));
        return this.getExistingPortal(pos, coordinateMultiplier)
                .map((result) -> this.convertTeleportResult(result, entity)).orElseGet(
                        () -> this.makePortal(pos).map((result) -> this.convertTeleportResult(result, entity))
                                .orElse(null));
    }

    private PortalInfo convertTeleportResult(BlockUtil.FoundRectangle result, Entity entity) {
        Direction.Axis axis = this.originPortalSize.getAxis();
        Vec3 vector3d = new Vec3(0.5D, 0.0D, 0.0D);
        return createPortalInfo(this.destinationWorld, result, axis, vector3d, entity.getDimensions(entity.getPose()),
                entity.getDeltaMovement(), entity.yRotO, entity.xRotO);
    }

    public Optional<BlockUtil.FoundRectangle> getExistingPortal(BlockPos searchOrigin,
                                                                double coordinateMultiplier) {
        PoiManager pointOfInterestManager = this.destinationWorld.getPoiManager();
        int i = coordinateMultiplier == 0.125D ? 16 : 128;
        pointOfInterestManager.ensureLoadedAndValid(this.destinationWorld, searchOrigin, i);
        Optional<PoiRecord> optionalPointOfInterest = pointOfInterestManager.getInSquare(
                        (pointOfInterestType) -> pointOfInterestType == ObjectHolder.PORTAL_POINT_OF_INTEREST_TYPE.get(),
                        searchOrigin, i, PoiManager.Occupancy.ANY).sorted(Comparator.<PoiRecord>comparingDouble(
                                (pointOfInterest) -> pointOfInterest.getPos().distSqr(searchOrigin))
                        .thenComparingInt((pointOfInterest) -> pointOfInterest.getPos().getY()))
                .filter((pointOfInterest) -> this.destinationWorld.getBlockState(pointOfInterest.getPos())
                        .hasProperty(BlockStateProperties.AXIS)).findFirst();
        return optionalPointOfInterest.map((pointOfInterest) ->
        {
            BlockPos poiPos = pointOfInterest.getPos();
            this.destinationWorld.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(poiPos), 3, poiPos);
            BlockState poiState = this.destinationWorld.getBlockState(poiPos);
            Pair<Direction.Axis, Direction.Axis> otherAxes =
                    Util.OTHER_AXES_MAP.get(poiState.getValue(BlockStateProperties.AXIS));
            // finds the destination portal size
            return BlockUtil
                    .getLargestRectangleAround(poiPos, otherAxes.getLeft(), PortalSize.MAX_SIZE, otherAxes.getRight(),
                            PortalSize.MAX_SIZE,
                            (lambdaPos) -> this.destinationWorld.getBlockState(lambdaPos) == poiState);
        });
    }

    public Optional<BlockUtil.FoundRectangle> makePortal(BlockPos pos) {
        double availablePortalLocDistance = -1.0D;
        BlockPos availablePortalLoc = null;
        double partiallyAvailablePortalLocDistance = -1.0D;
        BlockPos partiallyAvailablePortalLoc = null;
        WorldBorder worldBorder = this.destinationWorld.getWorldBorder();
        int maxPortalGenerationHeight = this.destinationWorld.getHeight() - 1;
        BlockPos.MutableBlockPos mutableOriginPos = pos.mutable();
        for (BlockPos.MutableBlockPos testPos : BlockPos.spiralAround(pos, 16, Direction.EAST, Direction.SOUTH)) {
            int maxPortalGenerationHeightForPos = Math.min(maxPortalGenerationHeight,
                    this.destinationWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, testPos.getX(), testPos.getZ()));
            if (worldBorder.isWithinBounds(testPos) && worldBorder.isWithinBounds(
                    testPos.move(this.originPortalSize.getDirA()).move(this.originPortalSize.getDirB()))) {
                testPos.move(this.originPortalSize.getOppositeDirA()).move(this.originPortalSize.getOppositeDirB());
                for (int availableYSpaceBottom = maxPortalGenerationHeightForPos; availableYSpaceBottom >= 0;
                     availableYSpaceBottom--) {
                    testPos.setY(availableYSpaceBottom);
                    if (this.destinationWorld.isEmptyBlock(testPos)) {
                        int availableYSpaceTop;
                        for (availableYSpaceTop = availableYSpaceBottom; availableYSpaceBottom > 0 &&
                                this.destinationWorld.isEmptyBlock(testPos.move(Direction.DOWN));
                             availableYSpaceBottom--) {
                        }
                        if (availableYSpaceBottom + this.originPortalSize.getSizeOnAxis(Direction.Axis.Y) + 1 <=
                                maxPortalGenerationHeight) {
                            int availableYSpace = availableYSpaceTop - availableYSpaceBottom;
                            if (availableYSpace <= 0 || availableYSpace >= this.originPortalSize.getSizeA() + 1) {
                                testPos.setY(availableYSpaceBottom);
                                if (this.checkRegionForPlacement(testPos, mutableOriginPos, Direction
                                        .fromAxisAndDirection(this.originPortalSize.getAxis(),
                                                Direction.AxisDirection.POSITIVE), 0)) {
                                    double distance = pos.distSqr(testPos);
                                    if (this.checkRegionForPlacement(testPos, mutableOriginPos, Direction
                                            .fromAxisAndDirection(this.originPortalSize.getAxis(),
                                                    Direction.AxisDirection.POSITIVE), -1) &&
                                            this.checkRegionForPlacement(testPos, mutableOriginPos, Direction
                                                    .fromAxisAndDirection(this.originPortalSize.getAxis(),
                                                            Direction.AxisDirection.POSITIVE), 1) &&
                                            (availablePortalLocDistance == -1.0D ||
                                                    availablePortalLocDistance > distance)) {
                                        availablePortalLocDistance = distance;
                                        availablePortalLoc = testPos.immutable();
                                    }
                                    if (availablePortalLocDistance == -1.0D &&
                                            (partiallyAvailablePortalLocDistance == -1.0D ||
                                                    partiallyAvailablePortalLocDistance > distance)) {
                                        partiallyAvailablePortalLocDistance = distance;
                                        partiallyAvailablePortalLoc = testPos.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (availablePortalLocDistance == -1.0D && partiallyAvailablePortalLocDistance != -1.0D) {
            availablePortalLoc = partiallyAvailablePortalLoc;
            availablePortalLocDistance = partiallyAvailablePortalLocDistance;
        }
        final int floorWidth = 1;
        if (availablePortalLocDistance == -1.0D) {
            availablePortalLoc =
                    (new BlockPos(pos.getX(), Mth.clamp(pos.getY(), 70, this.destinationWorld.getHeight() - 10),
                            pos.getZ())).immutable();
            Direction openPortalDir =
                    Direction.fromAxisAndDirection(this.originPortalSize.getAxis(), Direction.AxisDirection.POSITIVE);
            if (!worldBorder.isWithinBounds(availablePortalLoc)) {
                return Optional.empty();
            }
            BlockState airState = Blocks.AIR.defaultBlockState();
            if (this.originPortalSize.getAxis() != Direction.Axis.Y) {
                for (int floorXZ = -floorWidth; floorXZ <= floorWidth;
                     floorXZ++) // clears an area and creates the floor
                {
                    for (int portalXZ = 0; portalXZ < this.originPortalSize.getHorizontalSize(); portalXZ++) {
                        for (int y = -1; y < this.originPortalSize.getVerticalSize(); y++) {
                            BlockState blockState =
                                    y < 0 ? ObjectHolder.PORTAL_FRAME_BLOCK.get().defaultBlockState() : airState;
                            mutableOriginPos.setWithOffset(availablePortalLoc,
                                    portalXZ * this.originPortalSize.getDirA().getStepX() +
                                            floorXZ * openPortalDir.getStepX(), y,
                                    portalXZ * this.originPortalSize.getDirA().getStepZ() +
                                            floorXZ * openPortalDir.getStepZ());
                            this.destinationWorld.setBlockAndUpdate(mutableOriginPos, blockState);
                        }
                    }
                }
            } else {
                final int verticalAirSpace = 2;
                for (int y = -verticalAirSpace; y <= verticalAirSpace; y++) {
                    for (int x = 0; x < this.originPortalSize.getSizeOnAxis(Direction.Axis.X); x++) {
                        for (int z = 0; z < this.originPortalSize.getSizeOnAxis(Direction.Axis.Z); z++) {
                            mutableOriginPos.setWithOffset(availablePortalLoc, x, y, z);
                            this.destinationWorld.setBlockAndUpdate(mutableOriginPos, airState);
                        }
                    }
                }
            }
        }
        int portalSizeA = this.originPortalSize.getSizeA();
        int portalSizeB = this.originPortalSize.getSizeB();
        for (int dirAOffset = -1; dirAOffset < portalSizeA + 1; dirAOffset++) {
            for (int dirBOffset = -1; dirBOffset < portalSizeB + 1; dirBOffset++) {
                if (dirAOffset == -1 | dirAOffset == portalSizeA || dirBOffset == -1 || dirBOffset == portalSizeB) {
                    Vec3i offsetVector = new Vec3i(dirAOffset * this.originPortalSize.getDirA().getStepX() +
                            dirBOffset * this.originPortalSize.getDirB().getStepX(),
                            dirAOffset * this.originPortalSize.getDirA().getStepY() +
                                    dirBOffset * this.originPortalSize.getDirB().getStepY(),
                            dirAOffset * this.originPortalSize.getDirA().getStepZ() +
                                    dirBOffset * this.originPortalSize.getDirB().getStepZ());
                    mutableOriginPos.setWithOffset(availablePortalLoc, offsetVector.getX(), offsetVector.getY(),
                            offsetVector.getZ());
                    if (this.originPortalSize.getPortalControllerRelativePos().equals(new BlockPos(offsetVector))) {
                        this.destinationWorld.setBlock(mutableOriginPos,
                                ObjectHolder.PORTAL_CONTROLLER_BLOCK.get().defaultBlockState()
                                        .setValue(PortalControllerBlock.AXIS, this.originPortalSize.getAxis()), 3);
                        PortalControllerTileEntity portalControllerTileEntity =
                                (PortalControllerTileEntity) this.destinationWorld.getBlockEntity(mutableOriginPos);
                        portalControllerTileEntity.getInventory()
                                .insertItem(0, ExistingWorldControlItem.getItemForWorld(this.originWorld), false);
                        portalControllerTileEntity.setPortalColor(
                                this.originPortalSize.getPortalController(this.originWorld).getLeft().getPortalColor());
                    } else {
                        this.destinationWorld
                                .setBlock(mutableOriginPos, ObjectHolder.PORTAL_FRAME_BLOCK.get().defaultBlockState(),
                                        3);
                    }
                }
            }
        }
        BlockState portalBlockState = ObjectHolder.PORTAL_BLOCK.get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, this.originPortalSize.getAxis());
        for (int dirAOffset = 0; dirAOffset < portalSizeA; dirAOffset++) // creates portal
        {
            for (int dirBOffset = 0; dirBOffset < portalSizeB; dirBOffset++) {
                Vec3i offsetVector = new Vec3i(dirAOffset * this.originPortalSize.getDirA().getStepX() +
                        dirBOffset * this.originPortalSize.getDirB().getStepX(),
                        dirAOffset * this.originPortalSize.getDirA().getStepY() +
                                dirBOffset * this.originPortalSize.getDirB().getStepY(),
                        dirAOffset * this.originPortalSize.getDirA().getStepZ() +
                                dirBOffset * this.originPortalSize.getDirB().getStepZ());
                mutableOriginPos.setWithOffset(availablePortalLoc, offsetVector.getX(), offsetVector.getY(),
                        offsetVector.getZ());
                this.destinationWorld.setBlock(mutableOriginPos, portalBlockState, 18);
            }
        }
        return Optional
                .of(new BlockUtil.FoundRectangle(availablePortalLoc.immutable(), portalSizeA, portalSizeB));
    }

    private boolean checkRegionForPlacement(BlockPos originalPos, BlockPos.MutableBlockPos offsetPos, Direction axis,
                                            int offsetScale) {
        Vec3i combinedOffsetVector =
                new Vec3i(this.originPortalSize.getDirA().getStepX() + this.originPortalSize.getDirB().getStepX(),
                        this.originPortalSize.getDirA().getStepY() + this.originPortalSize.getDirB().getStepY(),
                        this.originPortalSize.getDirA().getStepZ() + this.originPortalSize.getDirB().getStepZ());
        for (int i = -1; i < this.originPortalSize.getSizeA(); i++) {
            for (int j = -1; j < this.originPortalSize.getSizeB(); j++) {
                offsetPos.setWithOffset(originalPos, combinedOffsetVector.getX() * i + axis.getStepX() * offsetScale,
                        combinedOffsetVector.getY() * j + axis.getStepY() * offsetScale,
                        combinedOffsetVector.getZ() * i + axis.getStepZ() * offsetScale);
                if (j < 0 && !this.destinationWorld.getBlockState(offsetPos).getMaterial().isSolid()) {
                    return false;
                }
                if (j >= 0 && !this.destinationWorld.isEmptyBlock(offsetPos)) {
                    return false;
                }
            }
        }
        return true;
    }
}
