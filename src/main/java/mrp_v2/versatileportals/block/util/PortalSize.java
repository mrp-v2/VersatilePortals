package mrp_v2.versatileportals.block.util;

import com.google.common.collect.Lists;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PortalSize
{
    public static final int MAX_WIDTH = 21;
    public static final int MAX_HEIGHT = 21;
    public static final int MIN_WIDTH = 1;
    public static final int MIN_HEIGHT = 2;
    private static final AbstractBlock.IExtendedPositionPredicate<Direction> PORTAL_FRAME_PREDICATE =
            (state, reader, pos, side) ->
            {
                Block block = state.getBlock();
                if (state.isIn(ObjectHolder.PORTAL_CONTROLLER_BLOCK))
                {
                    return ((PortalControllerBlock) block).isSideValidForPortal(state, reader, pos, side);
                } else
                {
                    return state.isIn(ObjectHolder.PORTAL_FRAME_BLOCK);
                }
            };
    private static final Function<BlockState, Boolean> PORTAL_CONTROLLER_PREDICATE =
            (state) -> state.isIn(ObjectHolder.PORTAL_CONTROLLER_BLOCK);
    private static final Function<BlockState, Boolean> PORTAL_PREDICATE =
            (state) -> state.isIn(ObjectHolder.PORTAL_BLOCK);
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int height;
    private int width;
    private int portalBlockCount;
    @Nullable private BlockPos bottomLeft;
    @Nullable private BlockPos portalControllerRelativePos;

    public PortalSize(IWorld world, BlockPos pos, Direction.Axis axis)
    {
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.getBottomLeft(pos, world);
        if (this.bottomLeft == null)
        {
            this.invalidate();
        } else
        {
            this.width = this.getWidth(world);
            if (this.width > 0)
            {
                this.height = this.getHeight(world);
            } else
            {
                this.invalidate();
            }
        }
        if (this.isValid())
        {
            Pair<PortalControllerTileEntity, Boolean> portalControllerResult = this.getPortalController(world);
            if (!portalControllerResult.getRight())
            {
                this.invalidate();
            }
            if (portalControllerResult.getLeft() != null)
            {
                this.portalControllerRelativePos = portalControllerResult.getLeft().getPos().subtract(this.bottomLeft);
                this.portalControllerRelativePos =
                        new BlockPos(-this.portalControllerRelativePos.getX(), this.portalControllerRelativePos.getY(),
                                this.portalControllerRelativePos.getZ());
            }
        }
    }

    protected PortalSize(PacketBuffer buffer)
    {
        this.bottomLeft = buffer.readBlockPos();
        this.height = buffer.readInt();
        this.width = buffer.readInt();
        this.portalBlockCount = buffer.readInt();
        this.axis = buffer.readEnumValue(Direction.Axis.class);
        this.rightDir = this.axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
    }

    private static boolean canConnect(BlockState state)
    {
        return state.isAir() || state.isIn(BlockTags.FIRE) || PORTAL_PREDICATE.apply(state);
    }

    public static List<PortalSize> readListFromBuffer(PacketBuffer buffer)
    {
        int length = buffer.readInt();
        List<PortalSize> sizes = Lists.newArrayListWithCapacity(length);
        for (int i = 0; i < length; i++)
        {
            sizes.add(new PortalSize(buffer));
        }
        return sizes;
    }

    public static void writeListToBuffer(List<PortalSize> sizes, PacketBuffer buffer)
    {
        buffer.writeInt(sizes.size());
        sizes.forEach((size) -> size.writeToBuffer(buffer));
    }

    public void writeToBuffer(PacketBuffer buffer)
    {
        buffer.writeBlockPos(this.bottomLeft);
        buffer.writeInt(this.height);
        buffer.writeInt(this.width);
        buffer.writeInt(this.portalBlockCount);
        buffer.writeEnumValue(this.axis);
    }

    public static Optional<PortalSize> tryGetEmptyPortalSize(IWorld world, BlockPos pos)
    {
        Optional<PortalSize> optionalSize = tryGetPortalSize(world, pos, PortalSize::isValidAndEmpty, Direction.Axis.X);
        if (optionalSize.isPresent())
        {
            return optionalSize;
        }
        return tryGetPortalSize(world, pos, PortalSize::isValidAndEmpty, Direction.Axis.Z);
    }

    public static Optional<PortalSize> tryGetPortalSize(IWorld world, BlockPos pos, Predicate<PortalSize> test,
            Direction.Axis axis)
    {
        return Optional.of(new PortalSize(world, pos, axis)).filter(test);
    }

    public boolean isValidAndEmpty()
    {
        return this.isValid() && this.portalBlockCount == 0;
    }

    public boolean isValid()
    {
        return this.bottomLeft != null &&
                this.width >= MIN_WIDTH &&
                this.width <= MAX_WIDTH &&
                this.height >= MIN_HEIGHT &&
                this.height <= MAX_HEIGHT;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    @Nullable public BlockPos getPortalControllerRelativePos()
    {
        return portalControllerRelativePos;
    }

    private void invalidate()
    {
        this.bottomLeft = null;
        this.height = 0;
        this.width = 0;
    }

    public void placePortalBlocks(IWorld world)
    {
        BlockState state =
                ObjectHolder.PORTAL_BLOCK.getDefaultState().with(BlockStateProperties.HORIZONTAL_AXIS, this.axis);
        BlockPos.getAllInBoxMutable(this.bottomLeft,
                this.bottomLeft.offset(Direction.UP, this.height - 1).offset(this.rightDir, this.width - 1))
                .forEach((pos) -> world.setBlockState(pos, state, 18));
    }

    public Pair<PortalControllerTileEntity, Boolean> getPortalController(IWorld world)
    {
        PortalControllerTileEntity portalControllerTileEntity = null;
        boolean found = false;
        if (this.isValid())
        {
            for (Pair<BlockPos, Optional<Direction>> frame : this.getFrameBlocks())
            {
                BlockState state = world.getBlockState(frame.getLeft());
                if (PORTAL_CONTROLLER_PREDICATE.apply(state))
                {
                    if (found)
                    {
                        return Pair.of(null, false);
                    }
                    TileEntity temp = world.getTileEntity(frame.getLeft());
                    portalControllerTileEntity =
                            temp instanceof PortalControllerTileEntity ? (PortalControllerTileEntity) temp : null;
                    found = true;
                }
            }
        }
        return Pair.of(portalControllerTileEntity, found);
    }

    public List<Pair<BlockPos, Optional<Direction>>> getFrameBlocks()
    {
        List<Pair<BlockPos, Optional<Direction>>> frame =
                Lists.newArrayListWithCapacity(this.height * 2 + this.width * 2 + 4);
        //corners
        frame.add(Pair.of(this.bottomLeft.down().offset(this.rightDir, -1), Optional.empty()));
        frame.add(Pair.of(this.bottomLeft.down().offset(this.rightDir, this.width), Optional.empty()));
        frame.add(Pair.of(this.bottomLeft.up(this.height).offset(this.rightDir, -1), Optional.empty()));
        frame.add(Pair.of(this.bottomLeft.up(this.height).offset(this.rightDir, this.width), Optional.empty()));
        // left side
        for (int i = 0; i < this.height; i++)
        {
            frame.add(Pair.of(this.bottomLeft.offset(this.rightDir, -1).up(i), Optional.of(this.rightDir)));
        }
        // right side
        for (int i = 0; i < this.height; i++)
        {
            frame.add(Pair.of(this.bottomLeft.offset(this.rightDir, this.width).up(i),
                    Optional.of(this.rightDir.getOpposite())));
        }
        // top side
        for (int i = 0; i < this.width; i++)
        {
            frame.add(Pair.of(this.bottomLeft.up(this.height).offset(this.rightDir, i), Optional.of(Direction.DOWN)));
        }
        // bottom side
        for (int i = 0; i < this.width; i++)
        {
            frame.add(Pair.of(this.bottomLeft.down().offset(this.rightDir, i), Optional.of(Direction.UP)));
        }
        return frame;
    }

    public boolean validate()
    {
        return this.isValid() && this.portalBlockCount == this.width * this.height;
    }

    private int getHeight(IWorld world)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = this.getDistanceFromTop(mutable, world);
        return i >= MIN_HEIGHT && i <= MAX_HEIGHT && this.isFrameTopValid(mutable, i, world) ? i : 0;
    }

    private int getDistanceFromTop(BlockPos.Mutable pos, IWorld world)
    {
        for (int i = 0; i < MAX_HEIGHT; i++)
        {
            pos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!PORTAL_FRAME_PREDICATE.test(world.getBlockState(pos), world, pos, this.rightDir))
            {
                return i;
            }
            pos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!PORTAL_FRAME_PREDICATE.test(world.getBlockState(pos), world, pos, this.rightDir.getOpposite()))
            {
                return i;
            }
            for (int j = 0; j < this.width; j++)
            {
                pos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState state = world.getBlockState(pos);
                if (!canConnect(state))
                {
                    return i;
                }
                if (state.isIn(ObjectHolder.PORTAL_BLOCK))
                {
                    this.portalBlockCount++;
                }
            }
        }
        return MAX_HEIGHT;
    }

    private boolean isFrameTopValid(BlockPos.Mutable pos, int distanceFromTop, IWorld world)
    {
        for (int i = 0; i < this.width; i++)
        {
            BlockPos.Mutable mutable =
                    pos.setPos(this.bottomLeft).move(Direction.UP, distanceFromTop).move(this.rightDir, i);
            if (!PORTAL_FRAME_PREDICATE.test(world.getBlockState(mutable), world, mutable, Direction.DOWN))
            {
                return false;
            }
        }
        return true;
    }

    @Nullable private BlockPos getBottomLeft(BlockPos pos, IWorld world)
    {
        for (int i = Math.max(0, pos.getY() - MAX_HEIGHT);
             pos.getY() > i && canConnect(world.getBlockState(pos.down()));
             pos = pos.down())
        {
        }
        Direction direction = this.rightDir.getOpposite();
        int j = this.getDistanceFromEdge(pos, direction, world) - 1;
        return j < 0 ? null : pos.offset(direction, j);
    }

    private int getDistanceFromEdge(BlockPos start, Direction direction, IWorld world)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int maximumSizeForDirection = direction.getAxis() == Direction.Axis.Y ? MAX_HEIGHT : MAX_WIDTH;
        for (int i = 0; i <= maximumSizeForDirection; i++)
        {
            mutable.setPos(start).move(direction, i);
            BlockState state = world.getBlockState(mutable);
            if (!canConnect(state))
            {
                if (PORTAL_FRAME_PREDICATE.test(state, world, mutable, direction.getOpposite()))
                {
                    return i;
                }
                break;
            }
            BlockState downState = world.getBlockState(mutable.move(Direction.DOWN));
            if (!PORTAL_FRAME_PREDICATE.test(downState, world, mutable, Direction.UP))
            {
                break;
            }
        }
        return 0;
    }

    private int getWidth(IWorld world)
    {
        int i = this.getDistanceFromEdge(this.bottomLeft, this.rightDir, world);
        return i >= MIN_WIDTH && i <= MAX_WIDTH ? i : 0;
    }

    public Pair<BlockPos, BlockPos> getBlockRange()
    {
        BlockPos a = this.bottomLeft.down().offset(this.rightDir, -1);
        BlockPos b = this.bottomLeft.up(this.height).offset(this.rightDir, this.width);
        return Pair.of(a, b);
    }

    @Override public int hashCode()
    {
        return Objects.hash(axis, bottomLeft, height, width);
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof PortalSize))
        {
            return false;
        }
        PortalSize that = (PortalSize) o;
        return height == that.height &&
                width == that.width &&
                axis == that.axis &&
                Objects.equals(bottomLeft, that.bottomLeft);
    }

    public Direction.Axis getAxis()
    {
        return axis;
    }
}
