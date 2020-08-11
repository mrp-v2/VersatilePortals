package mrp_v2.randomdimensions.inventory;

import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class PortalControllerItemStackHandler extends ItemStackHandler
        implements NonNullSupplier<IItemHandler>, IInventory
{

    public static final int SLOTS = 1;

    private final PortalControllerTileEntity portalController;

    public PortalControllerItemStackHandler(@Nullable PortalControllerTileEntity portalController)
    {
        super(SLOTS);
        this.portalController = portalController;
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

    @Override public void clear()
    {
        for (int i = 0; i < this.getSlots(); i++)
        {
            this.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override public int getSizeInventory()
    {
        return this.getSlots();
    }

    @Override public boolean isEmpty()
    {
        for (int i = 0; i < this.getSlots(); i++)
        {
            if (!this.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override public ItemStack decrStackSize(int index, int count)
    {
        return this.extractItem(index, count, false);
    }

    @Override public ItemStack removeStackFromSlot(int index)
    {
        return this.extractItem(index, this.getStackInSlot(index).getCount(), false);
    }

    @Override public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.setStackInSlot(index, stack);
    }

    @Override public void markDirty()
    {
        if (this.portalController != null)
        {
            this.portalController.markDirty();
        }
    }

    @Override public boolean isUsableByPlayer(PlayerEntity player)
    {
        return true;
    }
}
