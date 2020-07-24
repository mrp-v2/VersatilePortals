package mrp_v2.randomdimensions.block;

public class IndestructiblePortalBlock extends PortalBlock {

	public static final String ID = "indestructible_" + PortalBlock.ID;

	public IndestructiblePortalBlock() {
		super(ID, (properties) -> properties.hardnessAndResistance(-1.0F, 3600000.0F));
	}
}
