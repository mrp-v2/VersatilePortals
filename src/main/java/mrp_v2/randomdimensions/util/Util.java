package mrp_v2.randomdimensions.util;

import mrp_v2.randomdimensions.RandomDimensions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class Util {
	public static TranslationTextComponent makeTranslation(String id) {
		return new TranslationTextComponent(RandomDimensions.ID + "." + id);
	}

	public static TranslationTextComponent makeTranslation(String... idParts) {
		return makeTranslation(String.join(".", idParts));
	}

	public static int iGetColorR(int color) {
		return (color & 0xFF0000) >> 16;
	}

	public static int iGetColorG(int color) {
		return (color & 0x00FF00) >> 8;
	}

	public static int iGetColorB(int color) {
		return color & 0x0000FF;
	}

	public static float fGetColorR(int color) {
		return ((color & 0xFF0000) >> 16) / 255.0F;
	}

	public static float fGetColorG(int color) {
		return ((color & 0x00FF00) >> 8) / 255.0F;
	}

	public static float fGetColorB(int color) {
		return (color & 0x0000FF) / 255.0F;
	}

	public static int createColor(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

	public static BlockPos[] getNeighbors(BlockPos pos) {
		return new BlockPos[] {
				pos.up(),
				pos.down(),
				pos.north(),
				pos.south(),
				pos.east(),
				pos.west()
		};
	}
}
