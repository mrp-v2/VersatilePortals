package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class BasicSingleItem extends Item
{
    public static final ItemGroup MAIN_ITEM_GROUP = new ItemGroup(VersatilePortals.ID)
    {
        @Override @OnlyIn(Dist.CLIENT) public ItemStack createIcon()
        {
            return new ItemStack(ObjectHolder.PORTAL_CONTROLLER_BLOCK_ITEM.get());
        }
    };

    public BasicSingleItem()
    {
        this(properties -> properties);
    }

    public BasicSingleItem(Function<Properties, Properties> propertiesModifier)
    {
        super(propertiesModifier.apply(new Properties().maxStackSize(1).group(MAIN_ITEM_GROUP)));
    }
}
