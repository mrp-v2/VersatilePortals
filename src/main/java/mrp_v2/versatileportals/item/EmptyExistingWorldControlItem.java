package mrp_v2.versatileportals.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class EmptyExistingWorldControlItem extends BasicSingleItem
{
    public static final String ID = "empty_" + ExistingWorldControlItem.ID;

    public EmptyExistingWorldControlItem()
    {
        super();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack itemStack = ExistingWorldControlItem.getItemForWorld(world);
        player.setItemInHand(hand, itemStack);
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide);
    }
}
