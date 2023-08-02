package mrp_v2.versatileportals.client.renderer.color;

import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExistingWorldControlItemColorer implements ItemColor
{
    public static final ExistingWorldControlItemColorer INSTANCE = new ExistingWorldControlItemColorer();

    @Override public int getColor(ItemStack stack, int tint)
    {
        if (stack.getItem() instanceof ExistingWorldControlItem)
        {
            return ExistingWorldControlItem.getColorDataFromItem(stack);
        }
        return 0xFFFFFF;
    }
}
