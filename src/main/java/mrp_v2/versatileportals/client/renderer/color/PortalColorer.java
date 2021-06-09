package mrp_v2.versatileportals.client.renderer.color;

import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.world.ClientWorld;
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
        // we can't use the passed in IBlockDisplayReader, because it is a ChunkRenderCache, and will error if trying access blocks not in the chunk
        ClientWorld world = null;
        if (iBlockDisplayReader instanceof ChunkRenderCache)
        {
            for (Field field : ChunkRenderCache.class.getDeclaredFields())
            {
                if (field.getType() == World.class)
                {
                    field.setAccessible(true);
                    try
                    {
                        world = (ClientWorld) field.get(iBlockDisplayReader);
                        break;
                    } catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (world == null)
        {
            return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
        }
        if (blockState.is(ObjectHolder.PORTAL_FRAME_BLOCK.get()) ||
                blockState.is(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()))
        {
            return PortalFrameUtil.getColor(world, pos);
        }
        if (blockState.is(ObjectHolder.PORTAL_BLOCK.get()))
        {
            return PortalBlock.getColor(blockState, world, pos);
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }
}
