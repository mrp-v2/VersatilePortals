package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    /**
     * Post-placement, only server-side
     */
    @Override public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState oldState,
            boolean isMoving)
    {
        super.onBlockAdded(newState, world, pos, oldState, isMoving);
        PortalFrameUtil.sendUpdatePacket(oldState, pos, world);
    }

    /**
     * Post-removal, only server-side
     */
    @Override public void onReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState,
            boolean isMoving)
    {
        super.onReplaced(oldState, world, pos, newState, isMoving);
        PortalFrameUtil.sendUpdatePacket(oldState, pos, world);
    }
}
