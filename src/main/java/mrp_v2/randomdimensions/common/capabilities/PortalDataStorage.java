package mrp_v2.randomdimensions.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PortalDataStorage implements IStorage<IPortalDataCapability> {

	private static final String WORLD_ID_NBT_ID = "WorldID";
	private static final String LAST_PORTAL_VEC_NBT_ID = "LastPortalVec";
	private static final String TELEPORT_DIRECTION_NBT_ID = "TeleportDirection";

	@Override
	public INBT writeNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance, Direction side) {
		ListNBT list = new ListNBT();
		for (String worldID : instance.getWorldsWithPortalData()) {
			CompoundNBT compound = new CompoundNBT();
			compound.putString(WORLD_ID_NBT_ID, worldID);
			Vector3d vec = instance.getLastPortalVec(worldID);
			compound.putDouble(LAST_PORTAL_VEC_NBT_ID + "X", vec.x);
			compound.putDouble(LAST_PORTAL_VEC_NBT_ID + "Y", vec.y);
			compound.putDouble(LAST_PORTAL_VEC_NBT_ID + "Z", vec.z);
			compound.putInt(TELEPORT_DIRECTION_NBT_ID, instance.getTeleportDirection(worldID).getIndex());
		}
		return list;
	}

	@Override
	public void readNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance, Direction side,
			INBT nbt) {
		ListNBT list = (ListNBT) nbt;
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT compound = list.getCompound(i);
			String worldID = compound.getString(WORLD_ID_NBT_ID);
			double x = compound.getDouble(LAST_PORTAL_VEC_NBT_ID + "X");
			double y = compound.getDouble(LAST_PORTAL_VEC_NBT_ID + "Y");
			double z = compound.getDouble(LAST_PORTAL_VEC_NBT_ID + "Z");
			instance.setLastPortalVec(worldID, new Vector3d(x, y, z));
			instance.setTeleportDirection(worldID, Direction.byIndex(compound.getInt(TELEPORT_DIRECTION_NBT_ID)));
		}
	}
}
