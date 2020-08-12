package mrp_v2.randomdimensions.item;

public class ExistingWorldTeleportItem extends PortalControlItem
{
    public static final String ID = "existing_world_" + PortalControlItem.ID;
    public static final String WORLD_ID_NBT_ID = "WorldID";

    public ExistingWorldTeleportItem()
    {
        super(ID);
    }
}
