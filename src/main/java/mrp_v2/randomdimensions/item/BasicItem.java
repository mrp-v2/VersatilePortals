package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BasicItem extends Item {

	public static ItemGroup MAIN_ITEM_GROUP = new ItemGroup("random_dimensions") {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(ObjectHolder.PORTAL_CONTROLLER_BLOCK_ITEM);
		}
	};

	public BasicItem(Properties properties, String id) {
		super(properties.group(MAIN_ITEM_GROUP));
		this.setRegistryName(RandomDimensions.ID, id);
	}
}
