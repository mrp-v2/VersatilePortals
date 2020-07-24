package mrp_v2.randomdimensions.block;

import java.util.function.Function;

import net.minecraft.block.Blocks;

public class PortalFrameBlock extends BasicBlock {

	public static final String ID = "portal_frame";

	public PortalFrameBlock() {
		this(ID);
	}

	protected PortalFrameBlock(String id) {
		this(id, (properties) -> properties);
	}

	protected PortalFrameBlock(String id, Function<Properties, Properties> propertiesModifier) {
		super(id, propertiesModifier.apply(Properties.from(Blocks.IRON_BLOCK)));
	}
}
