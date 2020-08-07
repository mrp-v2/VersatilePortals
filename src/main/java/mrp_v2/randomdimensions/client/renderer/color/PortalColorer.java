package mrp_v2.randomdimensions.client.renderer.color;

import mrp_v2.randomdimensions.block.PortalBlock;
import mrp_v2.randomdimensions.block.PortalFrameBlock;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

@OnlyIn(Dist.CLIENT) public class PortalColorer implements IBlockColor
{

    public static final PortalColorer INSTANCE = new PortalColorer();

    @Override
    public int getColor(BlockState blockState, @Nullable IBlockDisplayReader iBlockDisplayReader,
            @Nullable BlockPos pos, int tint)
    {
        if (iBlockDisplayReader == null || pos == null)
        {
            return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
        }
        if (iBlockDisplayReader instanceof ChunkRenderCache)
        {
            for (Field field : ChunkRenderCache.class.getDeclaredFields())
            {
                if (field.getType() == World.class)
                {
                    field.setAccessible(true);
                    try
                    {
                        iBlockDisplayReader = (IBlockDisplayReader) field.get(iBlockDisplayReader);
                    } catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (iBlockDisplayReader == null)
        {
            return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
        }
        if (blockState.getBlock() instanceof PortalFrameBlock)
        {
            return PortalFrameBlock.getColor(iBlockDisplayReader, pos);
        }
        if (blockState.getBlock() instanceof PortalBlock)
        {
            return PortalBlock.getColor(blockState, iBlockDisplayReader, pos);
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }
}
