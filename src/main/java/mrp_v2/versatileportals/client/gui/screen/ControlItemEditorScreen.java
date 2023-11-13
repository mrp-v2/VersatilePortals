package mrp_v2.versatileportals.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.network.ControlItemEditedPacket;
import mrp_v2.versatileportals.network.PacketHandler;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ControlItemEditorScreen extends Screen {

    public static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(VersatilePortals.ID, "textures/gui/container/control_item_editor.png");
    public static int IMAGE_WIDTH = 176, IMAGE_HEIGHT = 110;
    private final Player player;
    private final InteractionHand hand;
    private final int initialColor;
    private ForgeSlider colorR, colorG, colorB;

    public ControlItemEditorScreen(int initialColor, Player player, InteractionHand hand) {
        super(ExistingWorldControlItem.DISPLAY_NAME);
        this.player = player;
        this.hand = hand;
        this.initialColor = initialColor;
    }


    @Override
    protected void init() {
        super.init();
        int i = (this.width - IMAGE_WIDTH) / 2;
        int j = (this.height - IMAGE_HEIGHT) / 2;
        this.addElements(i, j);
    }

    @Override
    public void removed() {
        int color = Util.createColor(colorR.getValueInt(), colorG.getValueInt(), colorB.getValueInt());
        player.getItemInHand(hand).addTagElement(ExistingWorldControlItem.COLOR_NBT_ID, IntTag.valueOf(color));
        int slot = this.hand == InteractionHand.MAIN_HAND ? this.player.getInventory().selected : 40;
        PacketHandler.INSTANCE.sendToServer(new ControlItemEditedPacket(slot, color));
        super.removed();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i1, int i2, float f1) {
        this.renderBg(poseStack);
        RenderSystem.disableDepthTest();
        super.render(poseStack, i1, i1, f1);
        PoseStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.translate((this.width - IMAGE_WIDTH) / 2, (this.height - IMAGE_HEIGHT) / 2, 0);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        renderLabels(poseStack);
        poseStack2.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    private void renderLabels(PoseStack poseStack) {
        this.font.draw(poseStack, this.title, 8, 6, 4210752);
    }

    protected void renderBg(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int i = (this.width - IMAGE_WIDTH) / 2;
        int j = (this.height - IMAGE_HEIGHT) / 2;
        blit(poseStack, i, j, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        RenderSystem.setShaderColor(this.colorR.getValueInt() / 255F, this.colorG.getValueInt() / 255F, this.colorB.getValueInt() / 255F, 1);
        RenderSystem.setShaderTexture(0, PortalControllerScreen.PORTAL_FRAME_TEXTURE);
        blit(poseStack, i + 135, j + 37, 0, 0, 32, 32, 32, 32);
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (super.keyPressed(p_96552_, p_96553_, p_96554_)) {
            return true;
        }
        if (this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(p_96552_, p_96553_))) {
            this.onClose();
            return true;
        }
        return false;
    }

    protected void addElements(int xStart, int yStart) {
        int sliderYSpacing = 4;
        int sliderYOffset = 19;
        int sliderXOffset = 10;
        Component suffix = MutableComponent.create(new LiteralContents(""));
        this.colorR = this.addRenderableWidget(
                new ForgeSlider(xStart + sliderXOffset, yStart + sliderYOffset, 120, 20, PortalControllerScreen.colorRLabel, suffix, 0, 255,
                        Util.iGetColorR(initialColor), 1, 0, true));
        this.colorG = this.addRenderableWidget(
                new ForgeSlider(xStart + sliderXOffset, yStart + sliderYOffset + 20 + sliderYSpacing, 120, 20, PortalControllerScreen.colorGLabel,
                        suffix, 0, 255, Util.iGetColorG(initialColor), 1, 0, true));
        this.colorB = this.addRenderableWidget(
                new ForgeSlider(xStart + sliderXOffset, yStart + sliderYOffset + 20 * 2 + sliderYSpacing * 2, 120, 20,
                        PortalControllerScreen.colorBLabel, suffix, 0, 255, Util.iGetColorB(initialColor), 1, 0, true));
    }
}
