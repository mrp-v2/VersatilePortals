package mrp_v2.randomdimensions.block;

public class IndestructiblePortalFrameBlock extends PortalFrameBlock {

	public static final String ID = "indestructible_" + PortalFrameBlock.ID;

	public IndestructiblePortalFrameBlock() {
		super(ID, (properties) -> properties.hardnessAndResistance(-1.0F, 3600000.0F));
	}
}
