package mrp_v2.versatileportals.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mrp_v2.versatileportals.blockentity.PortalControllerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PortalControllerBlockEntityRenderer
        implements BlockEntityRenderer<PortalControllerBlockEntity> {

    public PortalControllerBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(PortalControllerBlockEntity tileEntityIn, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack itemStack = tileEntityIn.getInventory().getStackInSlot(0);
        if (itemStack != ItemStack.EMPTY) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(360.0F * (tileEntityIn.ticks + partialTicks) /
                    PortalControllerBlockEntity.TICKS_PER_RENDER_REVOLUTION));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(itemStack, ItemTransforms.TransformType.FIXED, combinedLightIn,
                            combinedOverlayIn, poseStack, bufferIn, 0);
            poseStack.popPose();
        }
    }
}
