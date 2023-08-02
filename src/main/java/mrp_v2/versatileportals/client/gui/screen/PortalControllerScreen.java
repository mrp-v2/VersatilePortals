package mrp_v2.versatileportals.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.inventory.container.PortalControllerContainer;
import mrp_v2.versatileportals.network.PacketHandler;
import mrp_v2.versatileportals.network.PortalControllerScreenClosedPacket;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.Slider;

@OnlyIn(Dist.CLIENT)
public class PortalControllerScreen extends AbstractContainerScreen<PortalControllerContainer>
{
    public static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(VersatilePortals.ID, "textures/gui/container/portal_controller.png");
    public static final TranslatableComponent colorRLabel = new TranslatableComponent(
            "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.color.r"), colorGLabel =
            new TranslatableComponent(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.color.g"), colorBLabel =
            new TranslatableComponent(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.color.b"),
            controlItemLabel = new TranslatableComponent(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".gui.slotLabel.controlItem");
    private Slider colorR;
    private Slider colorG;
    private Slider colorB;

    public PortalControllerScreen(PortalControllerContainer screenContainer, Inventory inv,
                                  Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.imageHeight = PortalControllerContainer.Y_SIZE;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override protected void init()
    {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        addElements(i, j);
    }

    @Override
    public void render(PoseStack stack, int i1, int i2, float f1)
    {
        this.renderBackground(stack);
        super.render(stack, i1, i2, f1);
        this.renderTooltip(stack, i1, i2);
    }

    @Override
    protected void renderLabels(PoseStack stack, int x, int y)
    {
        super.renderLabels(stack, x, y);
        this.font.draw(stack, controlItemLabel, 8, 92, 4210752);
    }

    @Override
    protected void renderBg(PoseStack stack, float f1, int i1, int i2)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override public void removed()
    {
        PacketHandler.INSTANCE
                .sendToServer(new PortalControllerScreenClosedPacket(this.getCurrentColor(), this.menu.getPos()));
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    public int getCurrentColor()
    {
        return Util.createColor(this.colorR.getValueInt(), this.colorG.getValueInt(), this.colorB.getValueInt());
    }

    protected void addElements(int xStart, int yStart)
    {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int sliderYSpacing = 4;
        int sliderYOffset = 19;
        int sliderXOffset = 28;
        int color = this.menu.getColor();
        Button.OnPress buttonAction = (button) ->
        {
        };
        TextComponent suffix = new TextComponent("");
        this.colorR = this.addButton(
                new Slider(xStart + sliderXOffset, yStart + sliderYOffset, 120, 20, colorRLabel, suffix, 0, 255,
                        Util.iGetColorR(color), false, true, buttonAction));
        this.colorG = this.addButton(
                new Slider(xStart + sliderXOffset, yStart + sliderYOffset + 20 + sliderYSpacing, 120, 20, colorGLabel,
                        suffix, 0, 255, Util.iGetColorG(color), false, true, buttonAction));
        this.colorB = this.addButton(
                new Slider(xStart + sliderXOffset, yStart + sliderYOffset + 20 * 2 + sliderYSpacing * 2, 120, 20,
                        colorBLabel, suffix, 0, 255, Util.iGetColorB(color), false, true, buttonAction));
    }
}
