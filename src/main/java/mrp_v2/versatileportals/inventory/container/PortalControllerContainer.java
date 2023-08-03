package mrp_v2.versatileportals.inventory.container;

import mrp_v2.versatileportals.inventory.PortalControllerItemStackHandler;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.tileentity.PortalControllerBlockEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.IContainerFactory;

public class PortalControllerContainer extends AbstractContainerMenu {
    public static final int Y_SIZE = 218;
    private final PortalControllerItemStackHandler inventory;
    private final int color;
    private final BlockPos pos;

    public PortalControllerContainer(int id, Inventory playerInventoryIn) {
        //noinspection ConstantConditions
        this(id, playerInventoryIn, PortalControllerBlockEntity.DEFAULT_PORTAL_COLOR, null);
    }

    public PortalControllerContainer(int id, Inventory playerInventoryIn, int color, BlockPos pos) {
        this(id, playerInventoryIn, new PortalControllerItemStackHandler(null), color, pos);
    }

    public PortalControllerContainer(int id, Inventory playerInventoryIn,
                                     PortalControllerItemStackHandler inventoryIn, int color, BlockPos pos) {
        super(ObjectHolder.PORTAL_CONTROLLER_CONTAINER_TYPE.get(), id);
        this.inventory = inventoryIn;
        this.color = color;
        this.pos = pos;
        this.addSlots(playerInventoryIn);
    }

    public boolean hasControlItem() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    public int getColorFromControlItem() {
        if (!hasControlItem()) {
            throw new IllegalStateException("Cannot get color from control item because there is no control item!");
        }
        return ExistingWorldControlItem.getColorDataFromItem(inventory.getStackInSlot(0));
    }

    private void addSlots(Inventory playerInventory) {
        this.addSlot(new SlotItemHandler(this.inventory, 0, 26, 104));
        // player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, Y_SIZE - 82 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, Y_SIZE - 24));
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return this.color;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.moveItemStackTo(itemStack1, this.inventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    public static class Type extends MenuType<PortalControllerContainer>
            implements IContainerFactory<PortalControllerContainer> {
        public Type() {
            super(Type::factory);
        }

        private static PortalControllerContainer factory(int windowId, Inventory playerInv) {
            return new PortalControllerContainer(windowId, playerInv);
        }

        @Override
        public PortalControllerContainer create(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
            int color = extraData.readInt();
            BlockPos pos = extraData.readBlockPos();
            return new PortalControllerContainer(windowId, playerInv, color, pos);
        }
    }
}
