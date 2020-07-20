package mrp_v2.randomdimensions.client.gui.screen;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PortalControllerScreen extends Screen {

	public static final String ID = "portal_controller";

	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(RandomDimensions.ID,
			"textures/gui/container/portal_controller.png");

	private PortalControllerTileEntity portalController;

	private Button doneButton;
	private Button cancelButton;

	public PortalControllerScreen(PortalControllerTileEntity tileEntityIn) {
		super(tileEntityIn.getDisplayName());
		this.portalController = tileEntityIn;
	}

	@Override
	protected void func_231160_c_() { // make gui elements, e.g. buttons, text fields
		this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
		this.doneButton = this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 4 - 150,
				this.field_230709_l_ / 4 + 120 + 12, 150, 20, Util.makeTranslation(ID, "close_button"), (button) -> {
					doneButtonClicked();
				}));
	}

	private void doneButtonClicked() {
		this.field_230706_i_.displayGuiScreen(null);
	}
}
