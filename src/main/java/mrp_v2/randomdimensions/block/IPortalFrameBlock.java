package mrp_v2.randomdimensions.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPortalFrameBlock
{
    boolean isSideValidForPortal(BlockState state, IBlockReader world, BlockPos pos, Direction side);
}
