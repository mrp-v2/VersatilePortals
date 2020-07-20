package mrp_v2.randomdimensions.block;

import net.minecraft.block.Blocks;

public class PortalFrameBlock extends BasicBlock {

	public static final String ID = "portal_frame";

	public PortalFrameBlock() {
		this(ID);
	}

	protected PortalFrameBlock(String id) {
		super(id, Blocks.IRON_BLOCK);
	}
}
