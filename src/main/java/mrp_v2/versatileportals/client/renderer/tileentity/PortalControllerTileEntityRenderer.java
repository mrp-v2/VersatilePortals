package mrp_v2.versatileportals.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT) public class PortalControllerTileEntityRenderer
        extends BlockEntityRenderer<PortalControllerTileEntity>
{
    public PortalControllerTileEntityRenderer(BlockEntityRenderDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PortalControllerTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        ItemStack itemStack = tileEntityIn.getInventory().getStackInSlot(0);
        if (itemStack != ItemStack.EMPTY)
        {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360.0F * (tileEntityIn.ticks + partialTicks) /
                    PortalControllerTileEntity.TICKS_PER_RENDER_REVOLUTION));
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(itemStack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                            combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.popPose();
        }
    }
}
