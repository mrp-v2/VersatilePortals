package mrp_v2.versatileportals.block.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mrp_v2.versatileportals.block.IPortalFrame;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.tileentity.PortalControllerBlockEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class PortalSize
{
    public static final int MAX_SIZE = 21, MIN_SIZE = 1;
    public static final Codec<PortalSize> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(Direction.Axis.CODEC.fieldOf("Axis").forGetter((size) -> size.axis),
                    BlockPos.CODEC.fieldOf("FarthestDownNorthWestPortalLoc")
                            .forGetter((size) -> size.farthestDownNorthWestPortalLoc),
                    Codec.INT.fieldOf("SizeA").forGetter((size) -> size.sizeA),
                    Codec.INT.fieldOf("SizeB").forGetter((size) -> size.sizeB),
                    Codec.INT.fieldOf("PortalBlockCount").forGetter((size) -> size.portalBlockCount))
            .apply(builder, PortalSize::new));
    private final Direction.Axis axis;
    private final Direction dirA, dirB, oppositeDirA, oppositeDirB;
    private int sizeA, sizeB, portalBlockCount;
    /**
     * The location of the possible portal block that is farthest down, farthest south, and farthest west
     */
    @Nullable private BlockPos farthestDownNorthWestPortalLoc;
    /**
     * Where the portal controller is relative to {@link PortalSize#farthestDownNorthWestPortalLoc}
     */
    @Nullable private BlockPos portalControllerRelativePos;

    private PortalSize(Direction.Axis axis, BlockPos farthestDownNorthWestPortalLoc, int sizeA, int sizeB,
            int portalBlockCount)
    {
        this.axis = axis;
        Pair<Direction.Axis, Direction.Axis> otherAxes = Util.OTHER_AXES_MAP.get(axis);
        this.dirA = Direction.get(Direction.AxisDirection.POSITIVE, otherAxes.getLeft());
        this.oppositeDirA = this.dirA.getOpposite();
        this.dirB = Direction.get(Direction.AxisDirection.POSITIVE, otherAxes.getRight());
        this.oppositeDirB = this.dirB.getOpposite();
        this.farthestDownNorthWestPortalLoc = farthestDownNorthWestPortalLoc;
        this.sizeA = sizeA;
        this.sizeB = sizeB;
        this.portalBlockCount = portalBlockCount;
    }

    public PortalSize(BlockGetter world, BlockPos pos, Direction.Axis axis)
    {
        this.axis = axis;
        Pair<Direction.Axis, Direction.Axis> otherAxes = Util.OTHER_AXES_MAP.get(axis);
        this.dirA = Direction.get(Direction.AxisDirection.POSITIVE, otherAxes.getLeft());
        this.oppositeDirA = this.dirA.getOpposite();
        this.dirB = Direction.get(Direction.AxisDirection.POSITIVE, otherAxes.getRight());
        this.oppositeDirB = this.dirB.getOpposite();
        this.farthestDownNorthWestPortalLoc = this.getFarthestDownNorthWestPortalLoc(pos, world);
        if (this.farthestDownNorthWestPortalLoc == null)
        {
            this.invalidate();
        } else
        {
            this.sizeA = this.getSizeA(world);
            if (this.sizeA > 0)
            {
                this.sizeB = this.getSizeB(world);
            } else
            {
                this.invalidate();
            }
        }
        if (this.isValid())
        {
            Pair<PortalControllerBlockEntity, Boolean> portalControllerResult = this.getPortalController(world);
            if (!portalControllerResult.getRight())
            {
                this.invalidate();
            }
            if (portalControllerResult.getLeft() != null)
            {
                this.portalControllerRelativePos =
                        portalControllerResult.getLeft().getBlockPos().subtract(this.farthestDownNorthWestPortalLoc);
            }
        }
    }

    protected PortalSize(FriendlyByteBuf buffer)
    {
        this.farthestDownNorthWestPortalLoc = buffer.readBlockPos();
        this.sizeA = buffer.readInt();
        this.sizeB = buffer.readInt();
        this.portalBlockCount = buffer.readInt();
        this.axis = buffer.readEnum(Direction.Axis.class);
        Pair<Direction.Axis, Direction.Axis> otherAxes = Util.OTHER_AXES_MAP.get(axis);
        this.dirA = Direction.get(Direction.AxisDirection.POSITIVE, otherAxes.getLeft());
        this.oppositeDirA = this.dirA.getOpposite();
        this.dirB = Direction.get(Direction.AxisDirection.POSITIVE, otherAxes.getRight());
        this.oppositeDirB = this.dirB.getOpposite();
    }

    public static boolean isPortalController(BlockState state)
    {
        return state.is(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
    }

    public List<Pair<BlockPos, Optional<Direction>>> getFrameBlocks()
    {
        List<Pair<BlockPos, Optional<Direction>>> frame =
                Lists.newArrayListWithCapacity(this.sizeA * 2 + this.sizeB * 2 + 4);
        //corners
        frame.add(Pair.of(this.farthestDownNorthWestPortalLoc.relative(oppositeDirB).relative(oppositeDirA),
                Optional.empty()));
        frame.add(Pair.of(this.farthestDownNorthWestPortalLoc.relative(oppositeDirB).relative(this.dirA, this.sizeA),
                Optional.empty()));
        frame.add(Pair.of(this.farthestDownNorthWestPortalLoc.relative(this.dirB, this.sizeB).relative(oppositeDirA),
                Optional.empty()));
        frame.add(Pair.of(this.farthestDownNorthWestPortalLoc.relative(this.dirB, this.sizeB)
                .relative(this.dirA, this.sizeA), Optional.empty()));
        for (int i = 0; i < this.sizeB; i++)
        {
            // -A side
            frame.add(Pair.of(this.farthestDownNorthWestPortalLoc.relative(this.oppositeDirA).relative(dirB, i),
                    Optional.of(this.dirA)));
            // +A side
            frame.add(
                    Pair.of(this.farthestDownNorthWestPortalLoc.relative(this.dirA, this.sizeA).relative(this.dirB, i),
                            Optional.of(this.oppositeDirA)));
        }
        for (int i = 0; i < this.sizeA; i++)
        {
            // -B side
            frame.add(Pair.of(this.farthestDownNorthWestPortalLoc.relative(this.oppositeDirB).relative(this.dirA, i),
                    Optional.of(this.dirB)));
            // +B side
            frame.add(
                    Pair.of(this.farthestDownNorthWestPortalLoc.relative(this.dirB, this.sizeB).relative(this.dirA, i),
                            Optional.of(this.oppositeDirB)));
        }
        return frame;
    }

    public static List<PortalSize> readListFromBuffer(FriendlyByteBuf buffer)
    {
        int length = buffer.readInt();
        List<PortalSize> sizes = Lists.newArrayListWithCapacity(length);
        for (int i = 0; i < length; i++)
        {
            sizes.add(new PortalSize(buffer));
        }
        return sizes;
    }

    private static boolean canConnect(BlockState state)
    {
        return state.isAir() || state.is(BlockTags.FIRE) || isPortal(state);
    }

    public static void writeListToBuffer(List<PortalSize> sizes, FriendlyByteBuf buffer)
    {
        buffer.writeInt(sizes.size());
        sizes.forEach((size) -> size.writeToBuffer(buffer));
    }

    public static Optional<PortalSize> tryGetEmptyPortalSize(LevelAccessor world, BlockPos pos)
    {
        Optional<PortalSize> optionalSize = tryGetPortalSize(world, pos, PortalSize::isValidAndEmpty, Direction.Axis.X);
        if (optionalSize.isPresent())
        {
            return optionalSize;
        }
        optionalSize = tryGetPortalSize(world, pos, PortalSize::isValidAndEmpty, Direction.Axis.Z);
        if (optionalSize.isPresent())
        {
            return optionalSize;
        }
        return tryGetPortalSize(world, pos, PortalSize::isValidAndEmpty, Direction.Axis.Y);
    }

    public static boolean isPortal(BlockState state)
    {
        return state.is(ObjectHolder.PORTAL_BLOCK.get());
    }

    public static Optional<PortalSize> tryGetPortalSize(LevelAccessor world, BlockPos pos, Predicate<PortalSize> test,
            Direction.Axis axis)
    {
        return Optional.of(new PortalSize(world, pos, axis)).filter(test);
    }

    public static boolean isPortalFrame(BlockState state, BlockGetter reader, BlockPos pos, Direction side)
    {
        Block block = state.getBlock();
        if (state.is(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()))
        {
            return ((PortalControllerBlock) block).isSideValidForPortal(state, reader, pos, side);
        } else if (state.is(ObjectHolder.PORTAL_FRAME_BLOCK.get()))
        {
            return true;
        } else if (state.getBlock() instanceof IPortalFrame)
        {
            return ((IPortalFrame) state.getBlock()).isSideValidForPortal(state, reader, pos, side);
        } else
        {
            return false;
        }
    }

    public boolean isValidAndEmpty()
    {
        return this.isValid() && this.portalBlockCount == 0;
    }

    public boolean isValid()
    {
        return this.farthestDownNorthWestPortalLoc != null && this.sizeB >= MIN_SIZE && this.sizeB <= MAX_SIZE &&
                this.sizeA >= MIN_SIZE && this.sizeA <= MAX_SIZE;
    }

    public int getSizeOnAxis(Direction.Axis axis)
    {
        if (this.dirA.getAxis() == axis)
        {
            return this.sizeA;
        } else if (this.dirB.getAxis() == axis)
        {
            return this.sizeB;
        } else
        {
            return 1;
        }
    }

    /**
     * Assumes the portal is not a Y axis portal
     */
    public int getHorizontalSize()
    {
        if (this.axis == Direction.Axis.Y)
        {
            throw new IllegalStateException();
        }
        return this.dirA.getAxis() == Direction.Axis.Y ? this.sizeB : this.sizeA;
    }

    /**
     * Assumes the portal is not a Y axis portal
     */
    public int getVerticalSize()
    {
        if (this.axis == Direction.Axis.Y)
        {
            throw new IllegalStateException();
        }
        return this.dirA.getAxis() == Direction.Axis.Y ? this.sizeA : this.sizeB;
    }

    public Direction getDirA()
    {
        return dirA;
    }

    public Direction getDirB()
    {
        return dirB;
    }

    public Direction getOppositeDirA()
    {
        return oppositeDirA;
    }

    public Direction getOppositeDirB()
    {
        return oppositeDirB;
    }

    public int getSizeA()
    {
        return sizeA;
    }

    public int getSizeB()
    {
        return sizeB;
    }

    private void invalidate()
    {
        this.farthestDownNorthWestPortalLoc = null;
        this.sizeA = 0;
        this.sizeB = 0;
    }

    @Nullable public BlockPos getPortalControllerRelativePos()
    {
        return portalControllerRelativePos;
    }

    public Pair<PortalControllerBlockEntity, Boolean> getPortalController(BlockGetter world)
    {
        PortalControllerBlockEntity portalControllerBlockEntity = null;
        boolean found = false;
        if (this.isValid())
        {
            for (Pair<BlockPos, Optional<Direction>> frame : this.getFrameBlocks())
            {
                BlockState state = world.getBlockState(frame.getLeft());
                if (isPortalController(state))
                {
                    if (found)
                    {
                        return Pair.of(null, false);
                    }
                    BlockEntity temp = world.getBlockEntity(frame.getLeft());
                    portalControllerBlockEntity =
                            temp instanceof PortalControllerBlockEntity ? (PortalControllerBlockEntity) temp : null;
                    found = true;
                }
            }
        }
        return Pair.of(portalControllerBlockEntity, found);
    }

    @Nullable
    private BlockPos getFarthestDownNorthWestPortalLoc(BlockPos pos, BlockGetter world)
    {
        if (!canConnect(world.getBlockState(pos)))
        {
            return null;
        }
        for (int i = Math.max(this.dirB.getAxis() == Direction.Axis.Y ? 0 : Integer.MIN_VALUE,
                getOnAxis(pos, this.dirB.getAxis()) - MAX_SIZE); getOnAxis(pos, this.dirB.getAxis()) > i &&
                     canConnect(world.getBlockState(pos.relative(this.oppositeDirB)));
             pos = pos.relative(this.oppositeDirB))
        {
        }
        int j = this.getDistanceFromEdge(pos, this.oppositeDirA, this.dirA, this.oppositeDirB, this.dirB, world) - 1;
        return j < 0 ? null : pos.relative(this.oppositeDirA, j);
    }

    // Also verifies the testingDir side of the frame
    private int getDistanceFromEdge(BlockPos start, Direction movingDir, Direction oppositeMovingDir,
                                    Direction testingDir, Direction oppositeTestingDir, BlockGetter world)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int i = 0; i <= MAX_SIZE; i++)
        {
            mutable.set(start).move(movingDir, i);
            BlockState nextBlockState = world.getBlockState(mutable);
            if (!canConnect(nextBlockState))
            {
                if (isPortalFrame(nextBlockState, world, mutable, oppositeMovingDir))
                {
                    return i;
                }
                break;
            }
            BlockState sideState = world.getBlockState(mutable.move(testingDir));
            if (!isPortalFrame(sideState, world, mutable, oppositeTestingDir))
            {
                break;
            }
        }
        return 0;
    }

    public boolean isValidAndHasCorrectPortalBlockCount()
    {
        return this.isValid() && this.portalBlockCount == this.sizeB * this.sizeA;
    }

    private int getSizeB(BlockGetter world)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int i = this.getDistanceFromPositiveBSide(mutable, world);
        return i >= MIN_SIZE && i <= MAX_SIZE && this.isPositiveBFrameSideValid(mutable, i, world) ? i : 0;
    }

    public void writeToBuffer(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.farthestDownNorthWestPortalLoc);
        buffer.writeInt(this.sizeA);
        buffer.writeInt(this.sizeB);
        buffer.writeInt(this.portalBlockCount);
        buffer.writeEnum(this.axis);
    }

    public void placePortalBlocks(LevelAccessor world)
    {
        BlockState state =
                ObjectHolder.PORTAL_BLOCK.get().defaultBlockState().setValue(BlockStateProperties.AXIS, this.axis);
        BlockPos.betweenClosed(this.farthestDownNorthWestPortalLoc,
                this.farthestDownNorthWestPortalLoc.relative(dirA, this.sizeA - 1).relative(this.dirB, this.sizeB - 1))
                .forEach((pos) -> world.setBlock(pos, state, 18));
    }

    // Also verifies the A sides of the frame
    private int getDistanceFromPositiveBSide(BlockPos.MutableBlockPos pos, BlockGetter world)
    {
        for (int i = 0; i < MAX_SIZE; i++)
        {
            pos.set(this.farthestDownNorthWestPortalLoc).move(this.dirB, i).move(this.oppositeDirA);
            if (!isPortalFrame(world.getBlockState(pos), world, pos, this.dirA))
            {
                return i;
            }
            pos.set(this.farthestDownNorthWestPortalLoc).move(this.dirB, i).move(this.dirA, this.sizeA);
            if (!isPortalFrame(world.getBlockState(pos), world, pos, this.oppositeDirA))
            {
                return i;
            }
            for (int j = 0; j < this.sizeA; j++)
            {
                pos.set(this.farthestDownNorthWestPortalLoc).move(this.dirB, i).move(this.dirA, j);
                BlockState state = world.getBlockState(pos);
                if (!canConnect(state))
                {
                    return i;
                }
                if (state.is(ObjectHolder.PORTAL_BLOCK.get()))
                {
                    this.portalBlockCount++;
                }
            }
        }
        return MAX_SIZE;
    }

    private int getOnAxis(BlockPos pos, Direction.Axis axis)
    {
        switch (axis)
        {
            case X:
                return pos.getX();
            case Y:
                return pos.getY();
            case Z:
                return pos.getZ();
            default:
                throw new IllegalArgumentException();
        }
    }

    private boolean isPositiveBFrameSideValid(BlockPos.MutableBlockPos pos, int distanceFromSide, BlockGetter world)
    {
        for (int i = 0; i < this.sizeA; i++)
        {
            BlockPos.MutableBlockPos mutable =
                    pos.set(this.farthestDownNorthWestPortalLoc).move(this.dirB, distanceFromSide).move(this.dirA, i);
            if (!isPortalFrame(world.getBlockState(mutable), world, mutable, this.oppositeDirB))
            {
                return false;
            }
        }
        return true;
    }

    private int getSizeA(BlockGetter world)
    {
        int i = this.getDistanceFromEdge(this.farthestDownNorthWestPortalLoc, this.dirA, this.oppositeDirA,
                this.oppositeDirB, this.dirB, world);
        return i >= MIN_SIZE && i <= MAX_SIZE ? i : 0;
    }

    public Pair<BlockPos, BlockPos> getBlockRange()
    {
        BlockPos a = this.farthestDownNorthWestPortalLoc.relative(this.dirB, -1).relative(this.dirA, -1);
        BlockPos b =
                this.farthestDownNorthWestPortalLoc.relative(this.dirB, this.sizeB).relative(this.dirA, this.sizeA);
        return Pair.of(a, b);
    }

    @Override public int hashCode()
    {
        return Objects.hash(axis, farthestDownNorthWestPortalLoc, sizeA, sizeB);
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof PortalSize that))
        {
            return false;
        }
        return sizeA == that.sizeA && sizeB == that.sizeB && axis == that.axis &&
                Objects.equals(farthestDownNorthWestPortalLoc, that.farthestDownNorthWestPortalLoc);
    }

    public Direction.Axis getAxis()
    {
        return axis;
    }
}
