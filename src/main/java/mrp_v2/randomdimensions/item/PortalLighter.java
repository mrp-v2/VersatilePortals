package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.block.PortalBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class PortalLighter extends BasicSingleItem
{
    public static final String ID = "portal_lighter";

    public PortalLighter()
    {
        super((properties) -> properties.maxDamage(64), ID);
    }

    @SuppressWarnings("resource") @Override public ActionResultType onItemUse(ItemUseContext context)
    {
        if (PortalBlock.trySpawnPortal(context.getWorld(), context.getPos().offset(context.getFace())))
        {
            context.getWorld()
                   .playSound(context.getPlayer(), context.getPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE,
                           SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            context.getItem()
                   .damageItem(1, context.getPlayer(), (player) -> player.sendBreakAnimation(context.getHand()));
            return ActionResultType.func_233537_a_(context.getWorld().isRemote);
        }
        return ActionResultType.FAIL;
    }
}
