package mrp_v2.versatileportals.village;

import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class PortalPointOfInterestType extends PoiType
{
    public static final String ID = PortalBlock.ID;

    public PortalPointOfInterestType()
    {
        super(ID, getBlockStates(ObjectHolder.PORTAL_BLOCK.get()), 0, 1);
    }
}
