package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RandomWorldControlItem extends PortalControlItem
{
    public static final String ID = "random_world_" + PortalControlItem.ID;
    private static int randomWorldID = 0; // TODO keep id after restart, save to save data somewhere

    public RandomWorldControlItem()
    {
        super(ID, properties -> properties);
    }

    public static ResourceLocation generateRandomWorld(ItemStack stack)
    {
        ResourceLocation worldID = Util.makeResourceLocation("random_world_" + randomWorldID++);
        PortalControlItem.addTeleportDataToItem(stack, worldID);
        return worldID;
    }
}
