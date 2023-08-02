package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.VersatilePortals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;

public class PortalDataStorage implements IStorage<IPortalDataCapability>
{
    public static Tag write(IPortalDataCapability instance)
    {
        return IPortalDataCapability.CODEC.encodeStart(NbtOps.INSTANCE, instance)
                .resultOrPartial(VersatilePortals.LOGGER::error).orElse(new CompoundTag());
    }

    public static void read(IPortalDataCapability instance, Tag compound)
    {
        IPortalDataCapability.CODEC.parse(NbtOps.INSTANCE, compound)
                .resultOrPartial(VersatilePortals.LOGGER::error).ifPresent(data ->
        {
            instance.setRemainingPortalCooldown(data.getRemainingPortalCooldown());
            instance.setInPortalTime(data.getInPortalTime());
            instance.setInPortal(data.getInPortal());
            instance.setPortalPos(data.getPortalPos());
        });
    }

    @Override
    public Tag writeNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance,
            @Nullable Direction side)
    {
        return write(instance);
    }

    @Override public void readNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance,
                                  @Nullable Direction side, Tag nbt)
    {
        read(instance, nbt);
    }
}
