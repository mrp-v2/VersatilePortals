package mrp_v2.customteleporters.village;

import mrp_v2.customteleporters.CustomTeleporters;
import mrp_v2.customteleporters.util.ObjectHolder;
import net.minecraft.village.PointOfInterestType;

public class PortalPointOfInterestType extends PointOfInterestType
{
    public static final String ID = "portal";

    public PortalPointOfInterestType()
    {
        super(ID, getAllStates(ObjectHolder.PORTAL_BLOCK), 0, 1);
        this.setRegistryName(CustomTeleporters.ID, ID);
    }

    public PortalPointOfInterestType register()
    {
        PointOfInterestType.registerBlockStates(this);
        return this;
    }
}
