package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EmptyExistingWorldTeleportItem extends BasicSingleItem
{
    public static final String ID = "empty_" + ExistingWorldTeleportItem.ID;

    public EmptyExistingWorldTeleportItem()
    {
        super(ID);
    }

    @Override public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        if (world.func_234923_W_() == World.field_234918_g_)
        {
            return super.onItemUse(context);
        }
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        player.setHeldItem(hand, new ItemStack(() -> ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM));
        PortalControlItem.addDataToItem(player.getHeldItem(hand), Util.getWorldID(world));
        return ActionResultType.func_233537_a_(world.isRemote);
    }
}
