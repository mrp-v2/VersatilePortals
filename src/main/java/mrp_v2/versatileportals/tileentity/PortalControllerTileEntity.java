package mrp_v2.versatileportals.tileentity;

import com.mojang.serialization.DataResult;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.inventory.PortalControllerItemStackHandler;
import mrp_v2.versatileportals.inventory.container.PortalControllerContainer;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.tileentity.util.PortalControllerData;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class PortalControllerTileEntity extends TileEntity
        implements ICapabilityProvider, INamedContainerProvider, ITickableTileEntity, INameable
{
    public static final String ID = PortalControllerBlock.ID;
    public static final int DEFAULT_PORTAL_COLOR = 0x00FF00;
    public static final int ERROR_PORTAL_COLOR = 0xFFFFFF;
    public static final int TICKS_PER_RENDER_REVOLUTION = 120;
    private static final String DATA_NBT_ID = "PortalControllerData";
    private final PortalControllerItemStackHandler inventory;
    private final LazyOptional<PortalControllerItemStackHandler> inventoryLazyOptional;
    public int ticks;
    private ITextComponent customName;
    private int portalColor;

    public static TileEntityType<PortalControllerTileEntity> createTileEntity()
    {
        //noinspection ConstantConditions
        return TileEntityType.Builder.create(PortalControllerTileEntity::new,
                ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()).build(null);
    }

    public PortalControllerTileEntity()
    {
        super(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE.get());
        this.inventory = new PortalControllerItemStackHandler(this);
        this.inventoryLazyOptional = LazyOptional.of(() -> this.inventory);
        this.portalColor = DEFAULT_PORTAL_COLOR;
    }

    public PortalControllerItemStackHandler getInventory()
    {
        return inventory;
    }

    public ItemStack getControlItemStack()
    {
        return this.inventory.getStackInSlot(0);
    }

    @Nullable public RegistryKey<World> getTeleportDestination()
    {
        if (this.inventory.getStackInSlot(0).isEmpty())
        {
            return null;
        }
        return ExistingWorldControlItem.getTeleportDestination(this.inventory.getStackInSlot(0));
    }

    @Override public ITextComponent getName()
    {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Nullable @Override public ITextComponent getCustomName()
    {
        return this.customName;
    }

    public void setCustomName(@Nullable ITextComponent name)
    {
        this.customName = name;
    }

    public ITextComponent getDefaultName()
    {
        return new TranslationTextComponent(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get().getTranslationKey());
    }

    @Override public Container createMenu(int id, PlayerInventory playerInventoryIn, PlayerEntity playerIn)
    {
        return new PortalControllerContainer(id, playerInventoryIn, this.inventory, this.portalColor, this.getPos());
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
            PortalFrameUtil.updatePortals(PortalFrameUtil.getPortalSizes(this.pos, this.world, false));
        }
    }

    @Override public void read(BlockState state, CompoundNBT compound)
    {
        super.read(state, compound);
        if (compound.contains(DATA_NBT_ID, 10))
        {
            CompoundNBT dataNBT = compound.getCompound(DATA_NBT_ID);
            DataResult<PortalControllerData> dataResult =
                    PortalControllerData.CODEC.parse(NBTDynamicOps.INSTANCE, dataNBT);
            dataResult.resultOrPartial(VersatilePortals.LOGGER::error).ifPresent((data) ->
            {
                this.portalColor = data.getPortalColor();
                this.inventory.deserializeNBT(data.getInventoryData());
                this.customName = data.getCustomName();
            });
        }
    }

    @Override public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        PortalControllerData.CODEC.encodeStart(NBTDynamicOps.INSTANCE, new PortalControllerData(this))
                .resultOrPartial(VersatilePortals.LOGGER::error)
                .ifPresent((data) -> compound.put(DATA_NBT_ID, data));
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

    public void setPortalColor(int newPortalColor)
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
        this.world.notifyBlockUpdate(this.getPos(), state, state, 2);
    }

    public void onInventorySlotChanged()
    {
        this.markDirty();
        this.sendUpdateToClient();
    }

    @Override public void tick()
    {
        if (!this.world.isRemote)
        {
            return;
        }
        if (++this.ticks >= TICKS_PER_RENDER_REVOLUTION)
        {
            this.ticks = 0;
        }
        if (this.ticks % 4 == 0)
        {
            if (this.inventory.getStackInSlot(0).isEmpty())
            {
                return;
            }
            PortalControllerBlock.animateTick(this.getBlockState(), this.world, this.pos);
        }
    }

    @Override public ITextComponent getDisplayName()
    {
        return INameable.super.getDisplayName();
    }
}
