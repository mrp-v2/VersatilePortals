package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.item.Item;

import java.util.function.Function;

import net.minecraft.world.item.Item.Properties;

public class BasicSingleItem extends Item
{
    public BasicSingleItem()
    {
        this(properties -> properties);
    }

    public BasicSingleItem(Function<Properties, Properties> propertiesModifier)
    {
        super(propertiesModifier.apply(new Properties().stacksTo(1).tab(ObjectHolder.MAIN_ITEM_GROUP)));
    }
}
