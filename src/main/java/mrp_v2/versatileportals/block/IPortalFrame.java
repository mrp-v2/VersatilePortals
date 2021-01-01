package mrp_v2.versatileportals.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPortalFrame
{
    boolean isSideValidForPortal(BlockState state, IBlockReader world, BlockPos pos, Direction side);
}
