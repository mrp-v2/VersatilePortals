package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.block.util.PortalSize;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public interface IPortalControlItem
{
    @Nullable
    Entity teleportEntity(Entity entity, ServerLevel currentWorld, PortalSize portalSize,
            ItemStack itemStack);
}
