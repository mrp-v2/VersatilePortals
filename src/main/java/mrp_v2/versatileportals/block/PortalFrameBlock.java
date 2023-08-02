package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class PortalFrameBlock extends Block implements IPortalFrame
{
    public static final String ID = "portal_frame";

    public PortalFrameBlock()
    {
        this((properties) -> properties);
    }

    protected PortalFrameBlock(Function<Properties, Properties> propertiesModifier)
    {
        super(propertiesModifier.apply(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY)
                .requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    }

    /**
     * Post-placement, only server-side
     */
    @Override
    public void onPlace(BlockState newState, Level world, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        super.onPlace(newState, world, pos, oldState, isMoving);
        PortalFrameUtil.sendUpdatePacket(oldState, pos, world);
    }

    /**
     * Post-removal, only server-side
     */
    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState,
            boolean isMoving)
    {
        super.onRemove(oldState, world, pos, newState, isMoving);
        PortalFrameUtil.sendUpdatePacket(oldState, pos, world);
    }

    @Override
    public boolean isSideValidForPortal(BlockState state, BlockGetter world, BlockPos pos, Direction side)
    {
        return true;
    }
}
