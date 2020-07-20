package mrp_v2.randomdimensions.util;

import mrp_v2.randomdimensions.block.PortalBlock;
import mrp_v2.randomdimensions.block.PortalControllerBlock;
import mrp_v2.randomdimensions.block.PortalFrameBlock;
import mrp_v2.randomdimensions.item.PortalLighter;
import mrp_v2.randomdimensions.particle.PortalParticle;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tileentity.TileEntityType;

public class ObjectHolder {

	public static final PortalBlock PORTAL_BLOCK;
	public static final PortalControllerBlock PORTAL_CONTROLLER_BLOCK;
	public static final PortalFrameBlock PORTAL_FRAME_BLOCK;
	public static final BlockItem PORTAL_CONTROLLER_BLOCK_ITEM;
	public static final BlockItem PORTAL_FRAME_BLOCK_ITEM;
	public static final PortalLighter PORTAL_LIGHTER;
	public static final BasicParticleType PORTAL_PARTICLE;
	public static final TileEntityType<PortalControllerTileEntity> PORTAL_CONTROLLER_TILE_ENTITY_TYPE;
	static {
		PORTAL_BLOCK = new PortalBlock();
		PORTAL_CONTROLLER_BLOCK = new PortalControllerBlock();
		PORTAL_FRAME_BLOCK = new PortalFrameBlock();
		PORTAL_CONTROLLER_BLOCK_ITEM = PORTAL_CONTROLLER_BLOCK.createBlockItem();
		PORTAL_FRAME_BLOCK_ITEM = PORTAL_FRAME_BLOCK.createBlockItem();
		PORTAL_LIGHTER = new PortalLighter();
		PORTAL_PARTICLE = new PortalParticle();
		PORTAL_CONTROLLER_TILE_ENTITY_TYPE = PortalControllerTileEntity.createTileEntity();
	}
}
