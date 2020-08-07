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

    public static final int SLOTS = 2;

    private final PortalControllerTileEntity portalController;

    public PortalControllerItemStackHandler(@Nullable PortalControllerTileEntity portalController)
    {
        super(SLOTS);
        this.portalController = portalController;
    }

    @Override public IItemHandler get()
    {
        return this;
    }

    @Override
	public void clear() {
		for (int i = 0; i < super.getSlots(); i++) {
			super.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getSizeInventory() {
		return super.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < super.getSlots(); i++) {
			if (!super.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return super.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return super.extractItem(index, super.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		super.setStackInSlot(index, stack);
	}

	@Override
	public void markDirty() {
		if (this.portalController != null) {
			this.portalController.markDirty();
		}
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
}
