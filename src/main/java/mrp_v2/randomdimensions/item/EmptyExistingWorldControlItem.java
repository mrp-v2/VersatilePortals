package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
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
        if (world.func_234923_W_() == World.field_234918_g_)
        {
            return super.onItemRightClick(world, player, hand);
        }
        ItemStack itemStack = new ItemStack(() -> ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM);
        PortalControlItem.addTeleportDataToItem(itemStack, Util.getWorldID(world));
        ExistingWorldControlItem.addColorDataToItem(itemStack, getColorFromWorld(world));
        player.setHeldItem(hand, itemStack);
        return ActionResult.func_233538_a_(itemStack, world.isRemote);
    }

    private static int getColorFromWorld(World world)
    {
        return world.func_234923_W_().toString().hashCode() & 0xFFFFFF;
    }
}
