package mrp_v2.versatileportals.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EmptyExistingWorldControlItem extends BasicSingleItem
{
    public static final String ID = "empty_" + ExistingWorldControlItem.ID;

    public EmptyExistingWorldControlItem()
    {
        super();
    }

    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack itemStack = ExistingWorldControlItem.getItemForWorld(world);
        player.setItemInHand(hand, itemStack);
        return ActionResult.sidedSuccess(itemStack, world.isClientSide);
    }
}
