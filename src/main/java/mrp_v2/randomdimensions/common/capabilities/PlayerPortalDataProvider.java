package mrp_v2.randomdimensions.common.capabilities;

import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class PlayerPortalDataProvider extends PortalDataProvider
{
    public PlayerPortalDataProvider(AttachCapabilitiesEvent<Entity> event)
    {
        super(event, new PlayerPortalDataHandler());
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        if (cap == CapabilityHandler.PLAYER_PORTAL_DATA_CAPABILITY)
        {
            return this.portalDataHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override public CompoundNBT serializeNBT()
    {
        return (CompoundNBT) ObjectHolder.PLAYER_PORTAL_DATA_STORAGE.writeNBT(
                CapabilityHandler.PLAYER_PORTAL_DATA_CAPABILITY, (IPlayerPortalDataCapability) this.portalDataHandler,
                null);
    }

    @Override public void deserializeNBT(CompoundNBT nbt)
    {
        ObjectHolder.PLAYER_PORTAL_DATA_STORAGE.readNBT(CapabilityHandler.PLAYER_PORTAL_DATA_CAPABILITY,
                (IPlayerPortalDataCapability) this.portalDataHandler, null, nbt);
    }
}
