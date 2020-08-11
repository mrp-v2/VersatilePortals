package mrp_v2.randomdimensions.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.inventory.container.PortalControllerContainer;
import mrp_v2.randomdimensions.network.Packet;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.Slider;

@OnlyIn(Dist.CLIENT) public class PortalControllerScreen extends ContainerScreen<PortalControllerContainer>
{
    public static final String ID = "portal_controller";

    public static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(RandomDimensions.ID, "textures/gui/container/portal_controller.png");

    private Slider colorR;
    private Slider colorG;
    private Slider colorB;

    public PortalControllerScreen(PortalControllerContainer screenContainer, PlayerInventory inv,
            ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
        this.ySize = PortalControllerContainer.Y_SIZE;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    @Override protected void init()
    {
        super.init();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        addElements(i, j);
    }

    @Override public void render(MatrixStack stack, int i1, int i2, float f1)
    {
        this.renderBackground(stack);
        super.render(stack, i1, i2, f1);
        this.func_230459_a_(stack, i1, i2);
    }

    @Override protected void drawGuiContainerForegroundLayer(MatrixStack stack, int x, int y)
    {
        super.drawGuiContainerForegroundLayer(stack, x, y);
        this.font.func_238422_b_(stack, Util.makeTranslation(ID, "slot_label", "control_item"), 8, 92, 4210752);
    }

    @Override protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float f1, int i1, int i2)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(stack, i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override public void onClose()
    {
        Packet.Handler.INSTANCE.sendToServer(
                new Packet.PortalControllerScreenClosed(this.getCurrentColor(), this.container.getPos()));
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    public int getCurrentColor()
    {
        return Util.createColor(this.colorR.getValueInt(), this.colorG.getValueInt(), this.colorB.getValueInt());
    }

    protected void addElements(int xStart, int yStart)
    {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int sliderYSpacing = 4;
        int sliderYOffset = 19;
        int sliderXOffset = 28;
        int color = this.container.getColor();
        this.colorR = this.addButton(
                new Slider(xStart + sliderXOffset, yStart + sliderYOffset, 120, 20, Util.makeTranslation(ID, "color.r"),
                        new StringTextComponent(""), 0, 255, Util.iGetColorR(color), false, true, (button) ->
                {
                }));
        this.colorG = this.addButton(
                new Slider(xStart + sliderXOffset, yStart + sliderYOffset + 20 + sliderYSpacing, 120, 20,
                        Util.makeTranslation(ID, "color.g"), new StringTextComponent(""), 0, 255,
                        Util.iGetColorG(color), false, true, (button) ->
                {
                }));
        this.colorB = this.addButton(
                new Slider(xStart + sliderXOffset, yStart + sliderYOffset + 20 * 2 + sliderYSpacing * 2, 120, 20,
                        Util.makeTranslation(ID, "color.b"), new StringTextComponent(""), 0, 255,
                        Util.iGetColorB(color), false, true, (button) ->
                {
                }));
    }
}
