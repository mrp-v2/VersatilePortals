package mrp_v2.randomdimensions.client.gui.widget;

import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.text.ITextComponent;

public class BasicOptionSlider extends AbstractSlider {

	public BasicOptionSlider(int xPosition, int yPosition, int width, int height, ITextComponent text,
			double startPosition) {
		super(xPosition, yPosition, width, height, text, startPosition);
	}

	@Override
	protected void func_230979_b_() { // slider selected
	}

	@Override
	protected void func_230972_a_() { // slider value changed
		// TODO Auto-generated method stub

	}

}
