package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public abstract class PortalControlItem extends BasicSingleItem
{
    public static final String WORLD_ID_NBT_ID = "WorldID";
    protected static final String ID = "control";

    protected PortalControlItem(String id)
    {
        super(id);
    }

    public static void addDataToItem(ItemStack stack, String worldID)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putString(WORLD_ID_NBT_ID, worldID);
        stack.setTag(compound);
    }

    public static RegistryKey<World> getTeleportDestination(ItemStack stack)
    {
        return Util.createWorldKey(stack.getOrCreateTag().getString(WORLD_ID_NBT_ID));
    }

    @Override public boolean shouldSyncTag()
    {
        return false;
    }
}
