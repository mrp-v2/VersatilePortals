package mrp_v2.randomdimensions.block;

import com.google.common.collect.Lists;
import mrp_v2.randomdimensions.network.Packet;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.Util;
import mrp_v2.randomdimensions.world.util.WorldWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class PortalFrameBlock extends BasicBlock
{
    public static final String ID = "portal_frame";

    public PortalFrameBlock()
    {
        this(ID);
    }

    protected PortalFrameBlock(String id)
    {
        this(id, (properties) -> properties);
    }

    protected PortalFrameBlock(String id, Function<Properties, Properties> propertiesModifier)
    {
        super(id, propertiesModifier.apply(Properties.from(Blocks.LAPIS_BLOCK)));
    }

    public static int getColor(IWorld iWorld, BlockPos pos)
    {
        PortalControllerTileEntity controller = getPortalController(iWorld, pos);
        if (controller != null)
        {
            return controller.getPortalColor();
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }

    @Nullable public static PortalControllerTileEntity getPortalController(IWorld iWorld, BlockPos pos)
    {
        PortalControllerTileEntity testController = null;
        for (PortalBlock.Size size : getPortalSizes(pos, iWorld, false))
        {
            PortalControllerTileEntity testTileEntity = size.getPortalController(iWorld).getLeft();
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

    public static List<PortalBlock.Size> getPortalSizes(BlockPos pos, IWorld iWorld, boolean includeSelf)
    {
        List<PortalBlock.Size> portals = Lists.newArrayList();
        for (Pair<BlockPos, Direction.Axis> test : getPossiblePortalLocations(pos))
        {
            PortalBlock.Size size = new PortalBlock.Size(iWorld, test.getLeft(), test.getRight());
            if (size.isValid())
            {
                portals.add(size);
            }
        }
        if (includeSelf)
        {
            PortalBlock.Size size = new PortalBlock.Size(iWorld, pos, Direction.Axis.X);
            if (size.isValid())
            {
                portals.add(size);
            }
            size = new PortalBlock.Size(iWorld, pos, Direction.Axis.Z);
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
        return Util.makeArray(Pair.of(pos.up(), Direction.Axis.Z), Pair.of(pos.down(), Direction.Axis.Z),
                Pair.of(pos.up(), Direction.Axis.X), Pair.of(pos.down(), Direction.Axis.X),
                Pair.of(pos.north(), Direction.Axis.Z), Pair.of(pos.south(), Direction.Axis.Z),
                Pair.of(pos.east(), Direction.Axis.X), Pair.of(pos.west(), Direction.Axis.X));
    }

    public static Pair<BlockPos, Direction.Axis>[] getVerticalDiagonals(BlockPos pos)
    {
        return Util.makeArray(Pair.of(pos.up().north(), Direction.Axis.Z), Pair.of(pos.up().south(), Direction.Axis.Z),
                Pair.of(pos.up().east(), Direction.Axis.X), Pair.of(pos.up().west(), Direction.Axis.X),
                Pair.of(pos.down().north(), Direction.Axis.Z), Pair.of(pos.down().south(), Direction.Axis.Z),
                Pair.of(pos.down().east(), Direction.Axis.X), Pair.of(pos.down().west(), Direction.Axis.X));
    }

    public static void updatePortals(List<PortalBlock.Size> sizes)
    {
        for (PortalBlock.Size size : sizes)
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
            Minecraft.getInstance().worldRenderer.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        }
    }

    /**
     * Post-placement, only server-side
     */
    @Override public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState oldState,
            boolean isMoving)
    {
        super.onBlockAdded(newState, world, pos, oldState, isMoving);
        sendUpdatePacket(oldState, pos, world);
    }

    private static void sendUpdatePacket(BlockState oldState, BlockPos pos, World world)
    {
        Packet.Handler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)),
                new Packet.PortalFrameUpdate(pos, getUpdateSizes(oldState, pos, world)));
    }

    private static List<PortalBlock.Size> getUpdateSizes(BlockState oldState, BlockPos pos, World world)
    {
        List<PortalBlock.Size> sizes = Lists.newArrayList();
        sizes.addAll(getPortalSizes(pos, world, false));
        sizes.addAll(getPortalSizes(pos, new WorldWrapper(world, pos, oldState), true));
        return sizes;
    }

    /**
     * Post-removal, only server-side
     */
    @Override public void onReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState,
            boolean isMoving)
    {
        super.onReplaced(oldState, world, pos, newState, isMoving);
        sendUpdatePacket(oldState, pos, world);
    }
}
