package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.VersatilePortals;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PortalDataStorage {
    public static Tag write(IPortalDataCapability instance) {
        return IPortalDataCapability.CODEC.encodeStart(NbtOps.INSTANCE, instance)
                .resultOrPartial(VersatilePortals.LOGGER::error).orElse(new CompoundTag());
    }

    public static void read(IPortalDataCapability instance, Tag compound) {
        IPortalDataCapability.CODEC.parse(NbtOps.INSTANCE, compound)
                .resultOrPartial(VersatilePortals.LOGGER::error).ifPresent(data ->
                {
                    instance.setRemainingPortalCooldown(data.getRemainingPortalCooldown());
                    instance.setInPortalTime(data.getInPortalTime());
                    instance.setInPortal(data.getInPortal());
                    instance.setPortalPos(data.getPortalPos());
                });
    }

    public Tag writeNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance,
                        @Nullable Direction side) {
        return write(instance);
    }

    public void readNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance,
                        @Nullable Direction side, Tag nbt) {
        read(instance, nbt);
    }
}
