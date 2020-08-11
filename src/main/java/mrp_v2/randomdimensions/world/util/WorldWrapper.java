package mrp_v2.randomdimensions.world.util;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nullable;

public class WorldWrapper implements IBlockDisplayReader
{
    private final World world;
    private final BlockPos overridePos;
    private final BlockState overrideState;

    public WorldWrapper(World world, @Nullable BlockPos overridePos, @Nullable BlockState overrideState)
    {
        this.world = world;
        this.overridePos = overridePos;
        this.overrideState = overrideState;
    }

    public World getWorld()
    {
        return world;
    }

    @Override public float func_230487_a_(Direction direction, boolean b)
    {
        return world.func_230487_a_(direction, b);
    }

    @Override public WorldLightManager getLightManager()
    {
        return world.getLightManager();
    }

    @Override public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        return world.getBlockColor(blockPosIn, colorResolverIn);
    }

    @Nullable @Override public TileEntity getTileEntity(BlockPos pos)
    {
        return world.getTileEntity(pos);
    }

    @Override public BlockState getBlockState(BlockPos pos)
    {
        if (pos.equals(this.overridePos))
        {
            return this.overrideState;
        }
        return world.getBlockState(pos);
    }

    @Override public FluidState getFluidState(BlockPos pos)
    {
        return world.getFluidState(pos);
    }
}
