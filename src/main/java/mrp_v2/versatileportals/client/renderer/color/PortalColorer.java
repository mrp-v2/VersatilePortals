package mrp_v2.versatileportals.client.renderer.color;

import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.blockentity.PortalControllerBlockEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

@OnlyIn(Dist.CLIENT)
public class PortalColorer implements BlockColor
{
    public static final PortalColorer INSTANCE = new PortalColorer();

    @Override
    public int getColor(BlockState blockState, @Nullable BlockAndTintGetter iBlockDisplayReader,
            @Nullable BlockPos pos, int tint)
    {
        if (iBlockDisplayReader == null || pos == null)
        {
            return PortalControllerBlockEntity.ERROR_PORTAL_COLOR;
        }
        // we can't use the passed in IBlockDisplayReader, because it is a ChunkRenderCache, and will error if trying access blocks not in the chunk
        ClientLevel world = null;
        if (iBlockDisplayReader instanceof RenderChunkRegion)
        {
            for (Field field : RenderChunkRegion.class.getDeclaredFields())
            {
                if (field.getType() == Level.class)
                {
                    field.setAccessible(true);
                    try
                    {
                        world = (ClientLevel) field.get(iBlockDisplayReader);
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
            return PortalControllerBlockEntity.ERROR_PORTAL_COLOR;
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
        return PortalControllerBlockEntity.ERROR_PORTAL_COLOR;
    }
}
