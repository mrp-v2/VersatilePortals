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
        super(ID);
    }

    @Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack itemStack = ExistingWorldControlItem.getItemForWorld(world);
        player.setHeldItem(hand, itemStack);
        return ActionResult.func_233538_a_(itemStack, world.isRemote);
    }
}
