package mrp_v2.randomdimensions.village;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.village.PointOfInterestType;

public class PortalPointOfInterestType extends PointOfInterestType {

	public static final String ID = "portal";

	public PortalPointOfInterestType() {
		super(ID, ObjectHolder.getPortalBlockStates(), 0, 1);
		this.setRegistryName(RandomDimensions.ID, ID);
	}
}
