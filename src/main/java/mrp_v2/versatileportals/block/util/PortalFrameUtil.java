package mrp_v2.versatileportals.block.util;

import com.google.common.collect.Lists;
import mrp_v2.versatileportals.network.PacketHandler;
import mrp_v2.versatileportals.network.PortalFrameUpdatePacket;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.Util;
import mrp_v2.versatileportals.world.WorldWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PortalFrameUtil
{
    public static int getColor(IBlockReader world, BlockPos pos)
    {
        PortalControllerTileEntity controller = getPortalController(world, pos);
        if (controller != null)
        {
            return controller.getPortalColor();
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }

    @Nullable public static PortalControllerTileEntity getPortalController(IBlockReader world, BlockPos pos)
    {
        PortalControllerTileEntity testController = null;
        for (PortalSize size : getPortalSizes(pos, world, false))
        {
            PortalControllerTileEntity testTileEntity = size.getPortalController(world).getLeft();
            if (testTileEntity != null)
            {
                if (testController != null && testController != testTileEntity)
                {
                    return null;
                }
                testController = testTileEntity;
            }
        }
        return testController;
    }

    public static List<PortalSize> getPortalSizes(BlockPos pos, IBlockReader world, boolean includeSelf)
    {
        List<PortalSize> portals = Lists.newArrayList();
        for (Pair<BlockPos, Direction.Axis> test : getPossiblePortalLocations(pos))
        {
            PortalSize size = new PortalSize(world, test.getLeft(), test.getRight());
            if (size.isValid())
            {
                portals.add(size);
            }
        }
        if (includeSelf)
        {
            PortalSize size = new PortalSize(world, pos, Direction.Axis.X);
            if (size.isValid())
            {
                portals.add(size);
            }
            size = new PortalSize(world, pos, Direction.Axis.Z);
            if (size.isValid())
            {
                portals.add(size);
            }
            size = new PortalSize(world, pos, Direction.Axis.Y);
            if (size.isValid())
            {
                portals.add(size);
            }
        }
        return portals;
    }

    public static Pair<BlockPos, Direction.Axis>[] getPossiblePortalLocations(BlockPos pos)
    {
        return Util.mergePairArrays(getNeighbors(pos), getVerticalDiagonals(pos));
    }

    public static Pair<BlockPos, Direction.Axis>[] getNeighbors(BlockPos pos)
    {
        List<Pair<BlockPos, Direction.Axis>> tests = new ArrayList<>();
        for (Direction.Axis axis : Direction.Axis.values())
        {
            Pair<Direction.Axis, Direction.Axis> otherAxes = Util.OTHER_AXES_MAP.get(axis);
            tests.add(Pair.of(pos
                            .relative(Direction.fromAxisAndDirection(otherAxes.getLeft(), Direction.AxisDirection.POSITIVE)),
                    axis));
            tests.add(Pair.of(pos
                            .relative(Direction.fromAxisAndDirection(otherAxes.getLeft(), Direction.AxisDirection.NEGATIVE)),
                    axis));
            tests.add(Pair.of(pos
                            .relative(Direction.fromAxisAndDirection(otherAxes.getRight(), Direction.AxisDirection.POSITIVE)),
                    axis));
            tests.add(Pair.of(pos
                            .relative(Direction.fromAxisAndDirection(otherAxes.getRight(), Direction.AxisDirection.NEGATIVE)),
                    axis));
        }
        return tests.toArray(Util.makeArray());
    }

    public static Pair<BlockPos, Direction.Axis>[] getVerticalDiagonals(BlockPos pos)
    {
        List<Pair<BlockPos, Direction.Axis>> tests = new ArrayList<>();
        for (Direction.Axis axis : Direction.Axis.values())
        {
            Pair<Direction.Axis, Direction.Axis> otherAxes = Util.OTHER_AXES_MAP.get(axis);
            tests.add(Pair.of(
                    pos.relative(Direction.fromAxisAndDirection(otherAxes.getLeft(), Direction.AxisDirection.POSITIVE))
                            .relative(Direction
                                    .fromAxisAndDirection(otherAxes.getRight(), Direction.AxisDirection.POSITIVE)),
                    axis));
            tests.add(Pair.of(
                    pos.relative(Direction.fromAxisAndDirection(otherAxes.getLeft(), Direction.AxisDirection.POSITIVE))
                            .relative(Direction
                                    .fromAxisAndDirection(otherAxes.getRight(), Direction.AxisDirection.NEGATIVE)),
                    axis));
            tests.add(Pair.of(
                    pos.relative(Direction.fromAxisAndDirection(otherAxes.getLeft(), Direction.AxisDirection.NEGATIVE))
                            .relative(Direction
                                    .fromAxisAndDirection(otherAxes.getRight(), Direction.AxisDirection.POSITIVE)),
                    axis));
            tests.add(Pair.of(
                    pos.relative(Direction.fromAxisAndDirection(otherAxes.getLeft(), Direction.AxisDirection.NEGATIVE))
                            .relative(Direction
                                    .fromAxisAndDirection(otherAxes.getRight(), Direction.AxisDirection.NEGATIVE)),
                    axis));
        }
        return tests.toArray(Util.makeArray());
    }

    public static void sendUpdatePacket(BlockState oldState, BlockPos pos, World world)
    {
        PacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)),
                new PortalFrameUpdatePacket(pos, getUpdateSizes(oldState, pos, world)));
    }

    public static List<PortalSize> getUpdateSizes(BlockState oldState, BlockPos pos, World world)
    {
        List<PortalSize> sizes = Lists.newArrayList();
        sizes.addAll(getPortalSizes(pos, world, false));
        sizes.addAll(getPortalSizes(pos, new WorldWrapper(world, pos, oldState), true));
        return sizes;
    }

    public static void updatePortals(List<PortalSize> sizes)
    {
        for (PortalSize size : sizes)
        {
            Pair<BlockPos, BlockPos> range = size.getBlockRange();
            int x1 = range.getLeft().getX();
            int y1 = range.getLeft().getY();
            int z1 = range.getLeft().getZ();
            int x2 = range.getRight().getX();
            int y2 = range.getRight().getY();
            int z2 = range.getRight().getZ();
            if (x1 > x2)
            {
                int xt = x1;
                x1 = x2;
                x2 = xt;
            }
            if (y1 > y2)
            {
                int yt = y1;
                y1 = y2;
                y2 = yt;
            }
            if (z1 > z2)
            {
                int zt = z1;
                z1 = z2;
                z2 = zt;
            }
            Minecraft.getInstance().levelRenderer.setBlocksDirty(x1, y1, z1, x2, y2, z2);
        }
    }
}
