package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ExistingWorldControlItem extends PortalControlItem
{
    public static final String ID = "existing_world_" + PortalControlItem.ID;
    public static final String COLOR_NBT_ID = "Color";

    public ExistingWorldControlItem()
    {
        //noinspection ConstantConditions
        super(ID, (properties -> properties.group(null)));
    }

    public static int getColorDataFromItem(ItemStack stack)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        return compound.contains(COLOR_NBT_ID) ? compound.getInt(COLOR_NBT_ID) : 0x808080;
    }

    public static ItemStack getItemForWorld(World world)
    {
        ItemStack itemStack = new ItemStack(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM);
        PortalControlItem.addTeleportDataToItem(itemStack, new ResourceLocation(Util.getWorldID(world)));
        ExistingWorldControlItem.addColorDataToItem(itemStack, getColorFromWorld(world));
        return itemStack;
    }

    public static void addColorDataToItem(ItemStack stack, int color)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putInt(COLOR_NBT_ID, color);
        stack.setTag(compound);
    }

    private static int getColorFromWorld(World world)
    {
        return world.getDimensionKey().toString().hashCode() & 0xFFFFFF;
    }
}
