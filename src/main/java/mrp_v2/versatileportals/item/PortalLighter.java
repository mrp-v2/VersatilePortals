package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.block.util.PortalSize;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

import java.util.Optional;

public class PortalLighter extends BasicSingleItem {
    public static final String ID = "portal_lighter";

    public PortalLighter() {
        super(properties -> properties.durability(64));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Optional<PortalSize> optionalSize = PortalSize
                .tryGetEmptyPortalSize(context.getLevel(), context.getClickedPos().relative(context.getClickedFace()));
        if (optionalSize.isPresent()) {
            context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.FLINTANDSTEEL_USE,
                    SoundSource.BLOCKS, 1.0F, context.getLevel().getRandom().nextFloat() * 0.4F + 0.8F);
            optionalSize.get().placePortalBlocks(context.getLevel());
            context.getItemInHand()
                    .hurtAndBreak(1, context.getPlayer(), (player) -> player.broadcastBreakEvent(context.getHand()));
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return InteractionResult.FAIL;
    }
}
