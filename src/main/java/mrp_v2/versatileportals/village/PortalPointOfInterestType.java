package mrp_v2.versatileportals.village;

import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.village.PointOfInterestType;

public class PortalPointOfInterestType extends PointOfInterestType
{
    public static final String ID = PortalBlock.ID;

    public PortalPointOfInterestType()
    {
        super(ID, getAllStates(ObjectHolder.PORTAL_BLOCK.get()), 0, 1);
    }
}
