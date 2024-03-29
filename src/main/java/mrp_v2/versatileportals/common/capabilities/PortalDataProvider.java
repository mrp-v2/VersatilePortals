package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PortalDataProvider implements ICapabilitySerializable<CompoundTag> {
    protected final PortalDataHandler portalDataHandler;
    protected LazyOptional<IPortalDataCapability> portalDataHandlerLazyOptional;
    protected final boolean isPlayerEntity;
    protected final Entity entity;

    public PortalDataProvider(AttachCapabilitiesEvent<Entity> event) {
        this(event, new PortalDataHandler());
    }

    protected PortalDataProvider(AttachCapabilitiesEvent<Entity> event, PortalDataHandler dataHandler) {
        this.portalDataHandler = dataHandler;
        this.portalDataHandlerLazyOptional = LazyOptional.of(() -> this.portalDataHandler);
        entity = event.getObject();
        isPlayerEntity = entity instanceof ServerPlayer;
        event.addListener(this::invalidate);
    }

    private void invalidate() {
        portalDataHandlerLazyOptional.invalidate();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isPlayerEntity && !portalDataHandlerLazyOptional.isPresent() && entity.isAlive()) {
            portalDataHandlerLazyOptional = LazyOptional.of(() -> this.portalDataHandler);
        }
        return CapabilityHandler.GetPortalDataCapability().orEmpty(cap, portalDataHandlerLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        //noinspection ConstantConditions
        return (CompoundTag) ObjectHolder.PORTAL_DATA_STORAGE.writeNBT(CapabilityHandler.GetPortalDataCapability(),
                this.portalDataHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //noinspection ConstantConditions
        ObjectHolder.PORTAL_DATA_STORAGE.readNBT(CapabilityHandler.GetPortalDataCapability(), this.portalDataHandler, null,
                nbt);
    }
}
