package mrp_v2.randomdimensions.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerPortalDataStorage implements IStorage<IPlayerPortalDataCapability> {

	private static final String IN_PORTAL_TIME_NBT_ID = "InPortalTime";

	@Override
	public INBT writeNBT(Capability<IPlayerPortalDataCapability> capability, IPlayerPortalDataCapability instance,
			Direction side) {
		CompoundNBT compound = PortalDataStorage.write(instance);
		compound.putInt(IN_PORTAL_TIME_NBT_ID, instance.getInPortalTime());
		return compound;
	}

	@Override
	public void readNBT(Capability<IPlayerPortalDataCapability> capability, IPlayerPortalDataCapability instance,
			Direction side, INBT nbt) {
		CompoundNBT compound = (CompoundNBT) nbt;
		PortalDataStorage.read(instance, compound);
		instance.setInPortalTime(compound.getInt(IN_PORTAL_TIME_NBT_ID));
	}
}
