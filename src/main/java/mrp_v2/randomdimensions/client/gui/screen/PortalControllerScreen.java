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

@OnlyIn(Dist.CLIENT)
public class PortalControllerScreen extends ContainerScreen<PortalControllerContainer> {

	public static final String ID = "portal_controller";

	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(RandomDimensions.ID,
			"textures/gui/container/portal_controller.png");

	private Slider colorR;
	private Slider colorG;
	private Slider colorB;

	public PortalControllerScreen(PortalControllerContainer screenContainer, PlayerInventory inv,
			ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.ySize = PortalControllerContainer.Y_SIZE;
		this.field_238745_s_ = this.ySize - 94;
	}

	@Override
	public void func_230430_a_(MatrixStack stack, int i1, int i2, float f1) {
		this.func_230446_a_(stack);
		super.func_230430_a_(stack, i1, i2, f1);
		this.func_230459_a_(stack, i1, i2);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void func_230450_a_(MatrixStack stack, float f1, int i1, int i2) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_230706_i_.getTextureManager().bindTexture(GUI_TEXTURE);
		int i = (this.field_230708_k_ - this.xSize) / 2;
		int j = (this.field_230709_l_ - this.ySize) / 2;
		this.func_238474_b_(stack, i, j, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void func_231160_c_() {
		super.func_231160_c_();
		int i = (this.field_230708_k_ - this.xSize) / 2;
		int j = (this.field_230709_l_ - this.ySize) / 2;
		addElements(i, j);
	}

	protected void addElements(int xStart, int yStart) {
		this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
		int sliderYSpacing = 4;
		int sliderYOffset = 20;
		int sliderXOffset = 28;
		int color = this.container.getColor();
		this.colorR = this.func_230480_a_(new Slider(xStart + sliderXOffset,
				yStart + sliderYOffset, 120, 20,
				Util.makeTranslation(ID, "color.r"), new StringTextComponent(""), 0, 255,
				Util.iGetColorR(color), false, true, (button) -> {
				}));
		this.colorG = this.func_230480_a_(new Slider(xStart + sliderXOffset,
				yStart + sliderYOffset + 20 + sliderYSpacing, 120, 20,
				Util.makeTranslation(ID, "color.g"), new StringTextComponent(""), 0, 255,
				Util.iGetColorG(color), false, true, (button) -> {
				}));
		this.colorB = this.func_230480_a_(new Slider(xStart + sliderXOffset,
				yStart + sliderYOffset + 20 * 2 + sliderYSpacing * 2, 120, 20,
				Util.makeTranslation(ID, "color.b"), new StringTextComponent(""), 0, 255,
				Util.iGetColorB(color), false, true, (button) -> {
				}));
	}

	@Override
	public void func_231164_f_() {
		Packet.Handler.INSTANCE.sendToServer(new Packet.PortalColor(this.getCurrentColor(), this.container.getPos()));
		super.func_231164_f_();
		this.field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}

	public int getCurrentColor() {
		return Util.createColor(this.colorR.getValueInt(), this.colorG.getValueInt(), this.colorB.getValueInt());
	}
}
