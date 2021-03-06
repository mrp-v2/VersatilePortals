package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class PortalDataProvider implements ICapabilitySerializable<CompoundNBT>
{
    protected final PortalDataHandler portalDataHandler;
    protected final LazyOptional<PortalDataHandler> portalDataHandlerLazyOptional;

    public PortalDataProvider(AttachCapabilitiesEvent<Entity> event)
    {
        this(event, new PortalDataHandler());
    }

    protected PortalDataProvider(AttachCapabilitiesEvent<Entity> event, PortalDataHandler dataHandler)
    {
        this.portalDataHandler = dataHandler;
        this.portalDataHandlerLazyOptional = LazyOptional.of(() -> this.portalDataHandler);
        event.addListener(this.portalDataHandlerLazyOptional::invalidate);
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityHandler.PORTAL_DATA_CAPABILITY)
        {
            return this.portalDataHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override public CompoundNBT serializeNBT()
    {
        //noinspection ConstantConditions
        return (CompoundNBT) ObjectHolder.PORTAL_DATA_STORAGE.writeNBT(CapabilityHandler.PORTAL_DATA_CAPABILITY,
                this.portalDataHandler, null);
    }

    @Override public void deserializeNBT(CompoundNBT nbt)
    {
        //noinspection ConstantConditions
        ObjectHolder.PORTAL_DATA_STORAGE.readNBT(CapabilityHandler.PORTAL_DATA_CAPABILITY, this.portalDataHandler, null,
                nbt);
    }
}
