package mrp_v2.customteleporters.inventory;

import mrp_v2.customteleporters.item.PortalControlItem;
import mrp_v2.customteleporters.tileentity.PortalControllerTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PortalControllerItemStackHandler extends ItemStackHandler implements NonNullSupplier<IItemHandler>
{
    private final PortalControllerTileEntity portalController;

    public PortalControllerItemStackHandler(@Nullable PortalControllerTileEntity portalController)
    {
        super(1);
        this.portalController = portalController;
    }

    @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return stack.getItem() instanceof PortalControlItem;
    }

    @Override protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);
        if (portalController != null)
        {
            portalController.onInventorySlotChanged();
        }
    }

    @Override public IItemHandler get()
    {
        return this;
    }
}
