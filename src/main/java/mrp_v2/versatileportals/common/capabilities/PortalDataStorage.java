package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.VersatilePortals;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;

public class PortalDataStorage implements IStorage<IPortalDataCapability>
{
    @Override public INBT writeNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance,
            @Nullable Direction side)
    {
        return write(instance);
    }

    public static INBT write(IPortalDataCapability instance)
    {
        return IPortalDataCapability.CODEC.encodeStart(NBTDynamicOps.INSTANCE, instance)
                .resultOrPartial(VersatilePortals.LOGGER::error).orElse(new CompoundNBT());
    }

    @Override public void readNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance,
            @Nullable Direction side, INBT nbt)
    {
        read(instance, nbt);
    }

    public static void read(IPortalDataCapability instance, INBT compound)
    {
        IPortalDataCapability.CODEC.parse(NBTDynamicOps.INSTANCE, compound)
                .resultOrPartial(VersatilePortals.LOGGER::error).ifPresent(data ->
        {
            instance.setRemainingPortalCooldown(data.getRemainingPortalCooldown());
            instance.setInPortalTime(data.getInPortalTime());
            instance.setInPortal(data.getInPortal());
            instance.setPortalPos(data.getPortalPos());
        });
    }
}
