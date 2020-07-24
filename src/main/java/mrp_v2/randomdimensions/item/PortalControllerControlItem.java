package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.Util;
import net.minecraft.item.ItemStack;

public class PortalControllerControlItem extends BasicItem {

	public static final String SEED_NBT_ID = "Seed";

	public PortalControllerControlItem(String id) {
		super(new Properties().maxStackSize(1), id);
	}

	@Override
	public boolean shouldSyncTag() {
		return false;
	}

	public static int getSeed(ItemStack stack) {
		return stack.getOrCreateTag().getInt(SEED_NBT_ID);
	}

	public static void setRandomSeed(ItemStack stack) {
		stack.getOrCreateTag().putLong(SEED_NBT_ID, Util.RAND.nextLong());
	}
}
