package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.block.util.PortalSize;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import java.util.Optional;

public class PortalLighter extends BasicSingleItem
{
    public static final String ID = "portal_lighter";

    public PortalLighter()
    {
        super(properties -> properties.durability(64));
    }

    @SuppressWarnings("resource") @Override public ActionResultType useOn(ItemUseContext context)
    {
        Optional<PortalSize> optionalSize = PortalSize
                .tryGetEmptyPortalSize(context.getLevel(), context.getClickedPos().relative(context.getClickedFace()));
        if (optionalSize.isPresent())
        {
            context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.FLINTANDSTEEL_USE,
                    SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            optionalSize.get().placePortalBlocks(context.getLevel());
            context.getItemInHand()
                    .hurtAndBreak(1, context.getPlayer(), (player) -> player.broadcastBreakEvent(context.getHand()));
            return ActionResultType.sidedSuccess(context.getLevel().isClientSide);
        }
        return ActionResultType.FAIL;
    }
}
