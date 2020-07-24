package mrp_v2.randomdimensions.tileentity;

import javax.annotation.Nullable;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.block.PortalBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public class PortalControllerTileEntity extends TileEntity implements ICapabilityProvider, INamedContainerProvider {

	public static final String ID = "portal_controller";

	private static final String INVENTORY_NBT_ID = "Inventory";
	private static final String PORTAL_COLOR_NBT_ID = "PortalColor";
	public static int DEFAULT_PORTAL_COLOR = 0x00FF00;

	public static TileEntityType<PortalControllerTileEntity> createTileEntity() {
		TileEntityType<PortalControllerTileEntity> tileEntityType = TileEntityType.Builder
				.create(PortalControllerTileEntity::new, ObjectHolder.PORTAL_CONTROLLER_BLOCK).build(null);
		tileEntityType.setRegistryName(RandomDimensions.ID, ID);
		return tileEntityType;
	}

	private ITextComponent customName;
	private final PortalControllerItemStackHandler itemStackHandler;
	private LazyOptional<PortalControllerItemStackHandler> inventoryLazyOptional;
	private int portalColor;

	public PortalControllerTileEntity() {
		super(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE);
		this.itemStackHandler = new PortalControllerItemStackHandler(this);
		this.inventoryLazyOptional = LazyOptional.of(() -> this.itemStackHandler);
		this.portalColor = DEFAULT_PORTAL_COLOR;
	}

	@SuppressWarnings("static-method")
	public RegistryKey<World> getTeleportDestination() {
		return World.field_234920_i_;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) { // read
		super.read(state, compound);
		this.itemStackHandler.deserializeNBT(compound.getCompound(INVENTORY_NBT_ID));
		this.portalColor = compound.getInt(PORTAL_COLOR_NBT_ID);
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.customName != null ? this.customName : new TranslationTextComponent("container." + ID);
	}

	public void setCustomName(@Nullable ITextComponent name) {
		this.customName = name;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put(INVENTORY_NBT_ID, this.itemStackHandler.serializeNBT());
		compound.putInt(PORTAL_COLOR_NBT_ID, this.portalColor);
		return compound;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInventoryIn, PlayerEntity playerIn) {
		return new PortalControllerContainer(id, playerInventoryIn, this.itemStackHandler, this.portalColor,
				this.getPos());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return this.inventoryLazyOptional.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void remove() {
		super.remove();
		this.inventoryLazyOptional.invalidate();
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.portalColor = pkt.getNbtCompound().getInt(PORTAL_COLOR_NBT_ID);
		for (BlockPos pos : Util.getNeighbors(this.getPos())) {
			PortalBlock.rerenderPortal(this.world, pos);
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt(PORTAL_COLOR_NBT_ID, this.portalColor);
		int id = -1;
		return new SUpdateTileEntityPacket(this.getPos(), id, compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT compound = super.getUpdateTag();
		compound.putInt(PORTAL_COLOR_NBT_ID, this.portalColor);
		return compound;
	}

	public int getPortalColor() {
		return this.portalColor;
	}

	public void setPortalColor(int newPortalColor) {
		if (this.portalColor != newPortalColor) {
			this.portalColor = newPortalColor;
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2 | 4 | 16 | 32);
		}
	}
}
