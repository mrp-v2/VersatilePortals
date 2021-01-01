package mrp_v2.versatileportals.inventory;

import mrp_v2.versatileportals.item.IPortalControlItem;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PortalControllerItemStackHandler extends ItemStackHandler
{
    private final PortalControllerTileEntity portalController;

    public PortalControllerItemStackHandler(@Nullable PortalControllerTileEntity portalController)
    {
        super(1);
        this.portalController = portalController;
    }

    @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return stack.getItem() instanceof IPortalControlItem;
    }

    @Override protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);
        if (portalController != null)
        {
            portalController.onInventorySlotChanged();
        }
    }
}
