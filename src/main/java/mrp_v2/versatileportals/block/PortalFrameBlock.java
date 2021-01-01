package mrp_v2.versatileportals.block;

import mrp_v2.mrplibrary.mixininterfaces.IAdjustableAbstractBlockProperties;
import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.function.Function;

public class PortalFrameBlock extends Block implements IPortalFrame
{
    public static final String ID = "portal_frame";

    public PortalFrameBlock()
    {
        this((properties) -> properties);
    }

    protected PortalFrameBlock(Function<Properties, Properties> propertiesModifier)
    {
        super(propertiesModifier.apply(((IAdjustableAbstractBlockProperties) Properties.from(Blocks.LAPIS_BLOCK))
                .setMaterialColor(MaterialColor.LIGHT_GRAY)));
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

    @Override public boolean isSideValidForPortal(BlockState state, IBlockReader world, BlockPos pos, Direction side)
    {
        return true;
    }
}
