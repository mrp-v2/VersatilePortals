package mrp_v2.randomdimensions.block;

import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
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

    public static int getColor(IBlockDisplayReader iBlockDisplayReader, BlockPos pos)
    {
        PortalControllerTileEntity controller = getPortalController(iBlockDisplayReader, pos);
        if (controller != null)
        {
            return controller.getPortalColor();
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }

    @Nullable
    public static PortalControllerTileEntity getPortalController(IBlockDisplayReader iBlockDisplayReader, BlockPos pos)
    {
        PortalControllerTileEntity testController = null;
        for (Pair<BlockPos, Direction.Axis> test : getPossiblePortalLocations(pos))
        {
            PortalBlock.Size size = new PortalBlock.Size(iBlockDisplayReader, test.getLeft(), test.getRight());
            if (size.isValid())
            {
                PortalControllerTileEntity testTileEntity = size.getPortalController(iBlockDisplayReader);
                if (testTileEntity != null)
                {
                    if (testController != null && testController != testTileEntity)
                    {
                        return null;
                    }
                    testController = testTileEntity;
                }
            }
        }
        return testController;
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
}
