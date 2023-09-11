package mrp_v2.versatileportals.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import mrp_v2.versatileportals.inventory.container.PortalControllerMenu;
import mrp_v2.versatileportals.network.PacketHandler;
import mrp_v2.versatileportals.network.PortalControllerScreenClosedPacket;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ForgeSlider;

@OnlyIn(Dist.CLIENT)
public class PortalControllerScreen extends AbstractContainerScreen<PortalControllerMenu> {
    public static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(VersatilePortals.ID, "textures/gui/container/portal_controller.png");
    public static final ResourceLocation PORTAL_FRAME_TEXTURE =
            new ResourceLocation(VersatilePortals.ID, "textures/block/" + PortalFrameBlock.ID + ".png");
    public static final Component colorRLabel = MutableComponent.create(new TranslatableContents(
            "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.color.r", null, new Object[0])), colorGLabel =
            MutableComponent.create(new TranslatableContents(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.color.g", null, new Object[0])), colorBLabel =
            MutableComponent.create(new TranslatableContents(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.color.b", null, new Object[0])),
            controlItemLabel = MutableComponent.create(new TranslatableContents(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.slotLabel.controlItem", null, new Object[0])),
            matchControlItemLabel = MutableComponent.create(new TranslatableContents("block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.matchControlItemLabel", null, new Object[0]));
    private ForgeSlider colorR;
    private ForgeSlider colorG;
    private ForgeSlider colorB;

    public PortalControllerScreen(PortalControllerMenu screenContainer, Inventory inv,
                                  Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageHeight = PortalControllerMenu.Y_SIZE;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        addElements(this.leftPos, this.topPos);
    }

    @Override
    public void render(PoseStack stack, int i1, int i2, float f1) {
        this.renderBackground(stack);
        super.render(stack, i1, i2, f1);
        this.renderTooltip(stack, i1, i2);
    }

    @Override
    protected void renderLabels(PoseStack stack, int x, int y) {
        super.renderLabels(stack, x, y);
        this.font.draw(stack, controlItemLabel, 8, 92, 4210752);
    }

    @Override
    protected void renderBg(PoseStack stack, float f1, int i1, int i2) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.setShaderColor(this.colorR.getValueInt() / 255F, this.colorG.getValueInt() / 255F, this.colorB.getValueInt() / 255F, 1);
        RenderSystem.setShaderTexture(0, PORTAL_FRAME_TEXTURE);
        blit(stack, i + 135, j + 37, 0, 0, 32, 32, 32, 32);
    }

    @Override
    public void removed() {
        PacketHandler.INSTANCE
                .sendToServer(new PortalControllerScreenClosedPacket(this.getCurrentColor(), this.menu.getPos()));
        super.removed();
    }

    public int getCurrentColor() {
        return Util.createColor(this.colorR.getValueInt(), this.colorG.getValueInt(), this.colorB.getValueInt());
    }

    protected void addElements(int xStart, int yStart) {
        int sliderYSpacing = 4;
        int sliderYOffset = 19;
        int sliderXOffset = 10;
        int color = this.menu.getColor();
        Component suffix = MutableComponent.create(new LiteralContents(""));
        this.colorR = this.addRenderableWidget(
                new ForgeSlider(xStart + sliderXOffset, yStart + sliderYOffset, 120, 20, colorRLabel, suffix, 0, 255,
                        Util.iGetColorR(color), 1, 0, true));
        this.colorG = this.addRenderableWidget(
                new ForgeSlider(xStart + sliderXOffset, yStart + sliderYOffset + 20 + sliderYSpacing, 120, 20, colorGLabel,
                        suffix, 0, 255, Util.iGetColorG(color), 1, 0, true));
        this.colorB = this.addRenderableWidget(
                new ForgeSlider(xStart + sliderXOffset, yStart + sliderYOffset + 20 * 2 + sliderYSpacing * 2, 120, 20,
                        colorBLabel, suffix, 0, 255, Util.iGetColorB(color), 1, 0, true));
        this.addRenderableWidget(new ExtendedButton(xStart + 48, yStart + 102, 120, 20, matchControlItemLabel, this::matchControlItemButtonPressed));
    }

    private void matchControlItemButtonPressed(Button button) {
        if (menu.hasControlItem()) {
            int color = menu.getColorFromControlItem();
            this.colorR.setValue(Util.iGetColorR(color));
            this.colorG.setValue(Util.iGetColorG(color));
            this.colorB.setValue(Util.iGetColorB(color));
        }
    }
}
