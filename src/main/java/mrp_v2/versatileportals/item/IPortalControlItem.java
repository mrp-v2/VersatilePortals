package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.block.util.PortalSize;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public interface IPortalControlItem
{
    @Nullable Entity teleportEntity(Entity entity, ServerWorld currentWorld, PortalSize portalSize,
            ItemStack itemStack);
}
