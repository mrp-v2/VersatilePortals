package mrp_v2.randomdimensions.util;

import mrp_v2.randomdimensions.RandomDimensions;
import net.minecraft.util.text.TranslationTextComponent;

public class Util {
	public static TranslationTextComponent makeTranslation(String id) {
		return new TranslationTextComponent(RandomDimensions.ID + "." + id);
	}

	public static TranslationTextComponent makeTranslation(String... idParts) {
		return makeTranslation(String.join(".", idParts));
	}
}
