package mrp_v2.randomdimensions.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ExistingWorldControlItem extends PortalControlItem
{
    public static final String ID = "existing_world_" + PortalControlItem.ID;
    public static final String COLOR_NBT_ID = "Color";

    public ExistingWorldControlItem()
    {
        super(ID);
    }

    public static void addColorDataToItem(ItemStack stack, int color)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putInt(COLOR_NBT_ID, color);
        stack.setTag(compound);
    }

    public static int getColorDataFromItem(ItemStack stack)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        return compound.contains(COLOR_NBT_ID) ? compound.getInt(COLOR_NBT_ID) : 0x808080;
    }
}
