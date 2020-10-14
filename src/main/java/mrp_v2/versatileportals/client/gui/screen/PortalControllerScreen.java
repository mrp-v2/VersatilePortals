package mrp_v2.versatileportals.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.datagen.EN_USTranslationGenerator;
import mrp_v2.versatileportals.inventory.container.PortalControllerContainer;
import mrp_v2.versatileportals.network.PacketHandler;
import mrp_v2.versatileportals.network.PortalControllerScreenClosedPacket;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.Slider;

@OnlyIn(Dist.CLIENT) public class PortalControllerScreen extends ContainerScreen<PortalControllerContainer>
{
    public static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(VersatilePortals.ID, "textures/gui/container/portal_controller.png");
    public static final TranslationTextComponent colorRLabel, colorGLabel, colorBLabel, controlItemLabel;

    static
    {
        String stem = String.join(".", "block", VersatilePortals.ID, PortalControllerBlock.ID, "gui");
        controlItemLabel =
                EN_USTranslationGenerator.makeTextTranslation(stem + ".slotLabel.controlItem", "Control Item");
        String nameStem = "Portal Color ";
        stem = String.join(".", stem, "color");
        colorRLabel = EN_USTranslationGenerator.makeTextTranslation(stem + ".r", nameStem + "R: ");
        colorGLabel = EN_USTranslationGenerator.makeTextTranslation(stem + ".g", nameStem + "G: ");
        colorBLabel = EN_USTranslationGenerator.makeTextTranslation(stem + ".b", nameStem + "B: ");
    }

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

    /**
     * For static initialization
     */
    public static void staticInit()
    {
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
        this.renderHoveredTooltip(stack, i1, i2);
    }

    @Override protected void drawGuiContainerForegroundLayer(MatrixStack stack, int x, int y)
    {
        super.drawGuiContainerForegroundLayer(stack, x, y);
        this.font.func_243248_b(stack, controlItemLabel, 8, 92, 4210752);
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
        PacketHandler.INSTANCE.sendToServer(
                new PortalControllerScreenClosedPacket(this.getCurrentColor(), this.container.getPos()));
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
        Button.IPressable buttonAction = (button) ->
        {
        };
        StringTextComponent suffix = new StringTextComponent("");
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
