package mrp_v2.versatileportals.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PortalDataStorage implements IStorage<IPortalDataCapability>
{
    private static final String REMAINING_PORTAL_COOLDOWN_NBT_ID = "PortalCooldown";
    private static final String IN_PORTAL_TIME_NBT_ID = "InPortalTime";

    @Override
    public INBT writeNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance, Direction side)
    {
        return write(instance);
    }

    public static CompoundNBT write(IPortalDataCapability instance)
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt(REMAINING_PORTAL_COOLDOWN_NBT_ID, instance.getRemainingPortalCooldown());
        compound.putInt(IN_PORTAL_TIME_NBT_ID, instance.getInPortalTime());
        return compound;
    }

    @Override
    public void readNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance, Direction side,
            INBT nbt)
    {
        read(instance, (CompoundNBT) nbt);
    }

    public static void read(IPortalDataCapability instance, CompoundNBT compound)
    {
        instance.setRemainingPortalCooldown(compound.getInt(REMAINING_PORTAL_COOLDOWN_NBT_ID));
        instance.setInPortalTime(compound.getInt(IN_PORTAL_TIME_NBT_ID));
    }
}
