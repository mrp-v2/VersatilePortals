package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class PortalDataProvider implements ICapabilitySerializable<CompoundTag>
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

    @Override
    public CompoundTag serializeNBT()
    {
        //noinspection ConstantConditions
        return (CompoundTag) ObjectHolder.PORTAL_DATA_STORAGE.writeNBT(CapabilityHandler.PORTAL_DATA_CAPABILITY,
                this.portalDataHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        //noinspection ConstantConditions
        ObjectHolder.PORTAL_DATA_STORAGE.readNBT(CapabilityHandler.PORTAL_DATA_CAPABILITY, this.portalDataHandler, null,
                nbt);
    }
}
