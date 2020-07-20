package mrp_v2.randomdimensions.tileentity;

import javax.annotation.Nullable;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.client.gui.screen.PortalControllerScreen;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PortalControllerTileEntity extends TileEntity {

	public static final String ID = "portal_controller";

	public static TileEntityType<PortalControllerTileEntity> createTileEntity() {
		TileEntityType<PortalControllerTileEntity> tileEntityType = TileEntityType.Builder
				.create(PortalControllerTileEntity::new, ObjectHolder.PORTAL_CONTROLLER_BLOCK).build(null);
		tileEntityType.setRegistryName(RandomDimensions.ID, ID);
		return tileEntityType;
	}

	// TODO add controller fields

	public static int DEFAULT_PORTAL_COLOR = 0x00FF00;

	private int portalColor;

	private ITextComponent customName;

	public PortalControllerTileEntity() {
		super(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE);
		portalColor = DEFAULT_PORTAL_COLOR;
	}

	@Nullable
	public void openMenu(PlayerEntity player) {
		Minecraft.getInstance().displayGuiScreen(new PortalControllerScreen(this));
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) { // read
		super.func_230337_a_(state, compound);
	}

	public ITextComponent getDisplayName() {
		return this.customName != null ? this.customName : new TranslationTextComponent("container." + ID);
	}

	public int getPortalColor() {
		return portalColor;
	}

	public void setCustomName(@Nullable ITextComponent name) {
		this.customName = name;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		return compound;
	}
}
