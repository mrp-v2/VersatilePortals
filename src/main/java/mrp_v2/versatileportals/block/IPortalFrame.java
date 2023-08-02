package mrp_v2.versatileportals.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface IPortalFrame
{
    boolean isSideValidForPortal(BlockState state, BlockGetter world, BlockPos pos, Direction side);
}
