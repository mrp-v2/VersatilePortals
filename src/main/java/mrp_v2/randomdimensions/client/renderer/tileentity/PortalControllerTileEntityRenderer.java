package mrp_v2.randomdimensions.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT) public class PortalControllerTileEntityRenderer
        extends TileEntityRenderer<PortalControllerTileEntity>
{
    public PortalControllerTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PortalControllerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
            IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        ItemStack itemStack = tileEntityIn.getItemStackHandler().getStackInSlot(0);
        if (itemStack != ItemStack.EMPTY)
        {
            matrixStackIn.push();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(
                    360.0F * tileEntityIn.ticks / PortalControllerTileEntity.TICKS_PER_RENDER_REVOLUTION));
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance()
                     .getItemRenderer()
                     .renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn,
                             combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
    }
}
