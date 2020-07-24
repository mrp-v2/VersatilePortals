package mrp_v2.randomdimensions.util;

import java.util.Random;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.common.capabilities.CapabilityHandler;
import mrp_v2.randomdimensions.common.capabilities.IPortalDataCapability;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class Util {
	public static final Random RAND = new Random();

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

	public static IPortalDataCapability getPortalData(Entity entity) {
		return entity.getCapability(CapabilityHandler.PORTAL_DATA_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Could not get an IPortalDataCapability!"));
	}

	public static String getWorldID(World world) {
		return world.func_234923_W_().func_240901_a_().toString();
	}

	public static BlockPos[] getCollidingBlocks(AxisAlignedBB box) {
		int minX = (int) Math.floor(box.minX);
		int minY = (int) Math.floor(box.minY);
		int minZ = (int) Math.floor(box.minZ);
		int maxX = (int) Math.ceil(box.maxX);
		int maxY = (int) Math.ceil(box.maxY);
		int maxZ = (int) Math.ceil(box.maxZ);
		int xSize = maxX - minX;
		int ySize = maxY - minY;
		int zSize = maxZ - minZ;
		BlockPos[] blocks = new BlockPos[xSize * ySize * zSize];
		for (int i = 0; i < xSize; i++) {
			int x = i + minX;
			for (int j = 0; j < ySize; j++) {
				int y = j + minY;
				for (int k = 0; k < zSize; k++) {
					int z = k + minZ;
					blocks[(i * ySize + j) * zSize + k] = new BlockPos(x, y, z);
				}
			}
		}
		return blocks;
	}
}
