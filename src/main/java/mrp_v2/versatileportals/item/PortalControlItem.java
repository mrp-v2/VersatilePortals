package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.function.Function;

public abstract class PortalControlItem extends BasicSingleItem
{
    public static final String WORLD_ID_NBT_ID = "WorldID";
    protected static final String ID = "control";

    protected PortalControlItem(Function<Properties, Properties> propertiesModifier)
    {
        super(propertiesModifier);
    }

    public static void addTeleportDataToItem(ItemStack stack, ResourceLocation worldID)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putString(WORLD_ID_NBT_ID, worldID.toString());
        stack.setTag(compound);
        stack.setDisplayName(new StringTextComponent(worldID.getPath()));
    }

    public static RegistryKey<World> getTeleportDestination(ItemStack stack)
    {
        String worldID = stack.getOrCreateTag().getString(WORLD_ID_NBT_ID);
        return Util.createWorldKey(worldID);
    }
}
