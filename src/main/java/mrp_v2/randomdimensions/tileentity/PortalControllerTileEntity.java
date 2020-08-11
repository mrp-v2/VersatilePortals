package mrp_v2.randomdimensions.tileentity;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.block.PortalFrameBlock;
import mrp_v2.randomdimensions.inventory.PortalControllerItemStackHandler;
import mrp_v2.randomdimensions.inventory.container.PortalControllerContainer;
import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class PortalControllerTileEntity extends TileEntity implements ICapabilityProvider, INamedContainerProvider
{

    public static final String ID = "portal_controller";
    public static final int DEFAULT_PORTAL_COLOR = 0x00FF00;
    public static final int ERROR_PORTAL_COLOR = 0xFFFFFF;
    public static final int PORTAL_CONTROLLER_UPDATE_FLAGS = 0;
    private static final String INVENTORY_NBT_ID = "Inventory";
    private static final String PORTAL_COLOR_NBT_ID = "PortalColor";
    private final PortalControllerItemStackHandler itemStackHandler;
    private final LazyOptional<PortalControllerItemStackHandler> inventoryLazyOptional;
    private ITextComponent customName;
    private int portalColor;

    public static TileEntityType<PortalControllerTileEntity> createTileEntity()
    {
        TileEntityType<PortalControllerTileEntity> tileEntityType =
                TileEntityType.Builder.create(PortalControllerTileEntity::new, ObjectHolder.PORTAL_CONTROLLER_BLOCK)
                                      .build(null);
        tileEntityType.setRegistryName(RandomDimensions.ID, ID);
        return tileEntityType;
    }

    public PortalControllerTileEntity()
    {
        super(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE);
        this.itemStackHandler = new PortalControllerItemStackHandler(this);
        this.inventoryLazyOptional = LazyOptional.of(() -> this.itemStackHandler);
        this.portalColor = DEFAULT_PORTAL_COLOR;
    }

    public PortalControllerItemStackHandler getItemStackHandler()
    {
        return itemStackHandler;
    }

    @SuppressWarnings("static-method") public RegistryKey<World> getTeleportDestination()
    {
        return World.field_234920_i_;
    }

    @Override public ITextComponent getDisplayName()
    {
        return this.customName != null ? this.customName : Util.makeTranslation("container", ID);
    }

    public void setCustomName(@Nullable ITextComponent name)
    {
        this.customName = name;
    }

    @Override public Container createMenu(int id, PlayerInventory playerInventoryIn, PlayerEntity playerIn)
    {
        return new PortalControllerContainer(id, playerInventoryIn, this.itemStackHandler, this.portalColor,
                this.getPos());
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return this.inventoryLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        this.read(this.getBlockState(), pkt.getNbtCompound());
        if (this.world.isBlockLoaded(this.pos))
        {
            PortalFrameBlock.updatePortals(PortalFrameBlock.getPortalSizes(this.pos, this.world, false));
        }
    }

    @Override public void read(BlockState state, CompoundNBT compound)
    {
        super.read(state, compound);
        this.portalColor = compound.getInt(PORTAL_COLOR_NBT_ID);
        this.itemStackHandler.deserializeNBT(compound.getCompound(INVENTORY_NBT_ID));
    }

    @Override public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.put(INVENTORY_NBT_ID, this.itemStackHandler.serializeNBT());
        compound.putInt(PORTAL_COLOR_NBT_ID, this.portalColor);
        return compound;
    }

    @Override public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.getPos(), -1, this.write(new CompoundNBT()));
    }

    @Override public CompoundNBT getUpdateTag()
    {
        return this.write(super.getUpdateTag());
    }

    @Override public void remove()
    {
        this.inventoryLazyOptional.invalidate();
        super.remove();
    }

    public int getPortalColor()
    {
        return this.portalColor;
    }

    public void onScreenClosed(int newPortalColor)
    {
        if (this.portalColor != newPortalColor)
        {
            this.portalColor = newPortalColor;
            this.markDirty();
            this.sendUpdateToClient();
        }
    }

    private void sendUpdateToClient()
    {
        BlockState state = this.getBlockState();
        this.world.notifyBlockUpdate(this.getPos(), state, state, 2 | PORTAL_CONTROLLER_UPDATE_FLAGS);
    }

    public void onInventorySlotChanged()
    {
        this.sendUpdateToClient();
    }
}
