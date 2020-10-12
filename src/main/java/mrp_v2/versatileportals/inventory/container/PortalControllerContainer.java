package mrp_v2.versatileportals.inventory.container;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.inventory.PortalControllerItemStackHandler;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.items.SlotItemHandler;

public class PortalControllerContainer extends Container
{
    public static final String ID = "portal_controller";
    public static final int Y_SIZE = 218;
    private final PortalControllerItemStackHandler inventory;
    private final int color;
    private final BlockPos pos;

    public PortalControllerContainer(int id, PlayerInventory playerInventoryIn)
    {
        this(id, playerInventoryIn, PortalControllerTileEntity.DEFAULT_PORTAL_COLOR, null);
    }

    public PortalControllerContainer(int id, PlayerInventory playerInventoryIn, int color, BlockPos pos)
    {
        this(id, playerInventoryIn, new PortalControllerItemStackHandler(null), color, pos);
    }

    public PortalControllerContainer(int id, PlayerInventory playerInventoryIn,
            PortalControllerItemStackHandler inventoryIn, int color, BlockPos pos)
    {
        super(ObjectHolder.PORTAL_CONTROLLER_CONTAINER_TYPE, id);
        this.inventory = inventoryIn;
        this.color = color;
        this.pos = pos;
        this.addSlots(playerInventoryIn);
    }

    private void addSlots(PlayerInventory playerInventory)
    {
        this.addSlot(new SlotItemHandler(this.inventory, 0, 80, 104));
        // player inventory slots
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, Y_SIZE - 82 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, Y_SIZE - 24));
        }
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    @OnlyIn(Dist.CLIENT) public int getColor()
    {
        return this.color;
    }

    @Override public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if (index < this.inventory.getSlots())
            {
                if (!this.mergeItemStack(itemStack1, this.inventory.getSlots(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, this.inventory.getSlots(), false))
            {
                return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            } else
            {
                slot.onSlotChanged();
            }
        }
        return itemStack;
    }

    @Override public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }

    public static class Type extends ContainerType<PortalControllerContainer>
            implements IContainerFactory<PortalControllerContainer>
    {

        public Type()
        {
            super(Type::factory);
            this.setRegistryName(VersatilePortals.ID, ID);
        }

        private static PortalControllerContainer factory(int windowId, PlayerInventory playerInv)
        {
            return new PortalControllerContainer(windowId, playerInv);
        }

        @Override
        public PortalControllerContainer create(int windowId, PlayerInventory playerInv, PacketBuffer extraData)
        {
            int color = extraData.readInt();
            BlockPos pos = extraData.readBlockPos();
            return new PortalControllerContainer(windowId, playerInv, color, pos);
        }
    }
}
