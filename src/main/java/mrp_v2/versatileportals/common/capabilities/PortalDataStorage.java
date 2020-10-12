package mrp_v2.versatileportals.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PortalDataStorage implements IStorage<IPortalDataCapability>
{
    private static final String WORLD_LIST_NBT_ID = "Worlds";
    private static final String REMAINING_PORTAL_COOLDOWN_NBT_ID = "PortalCooldown";
    private static final String IN_PORTAL_TIME_NBT_ID = "InPortalTime";
    private static final String WORLD_ID_NBT_ID = "WorldID";
    private static final String LAST_PORTAL_VEC_NBT_ID = "LastPortalVec";
    private static final String TELEPORT_DIRECTION_NBT_ID = "TeleportDirection";
    private static final String X_NBT_ID = "x";
    private static final String Y_NBT_ID = "y";
    private static final String Z_NBT_ID = "z";

    @Override
    public INBT writeNBT(Capability<IPortalDataCapability> capability, IPortalDataCapability instance, Direction side)
    {
        return write(instance);
    }

    public static CompoundNBT write(IPortalDataCapability instance)
    {
        CompoundNBT compound = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (String worldID : instance.getWorldsWithPortalData())
        {
            CompoundNBT listItem = new CompoundNBT();
            listItem.putString(WORLD_ID_NBT_ID, worldID);
            listItem.put(LAST_PORTAL_VEC_NBT_ID, writeVector3d(instance.getLastPortalVec(worldID)));
            listItem.putInt(TELEPORT_DIRECTION_NBT_ID, instance.getTeleportDirection(worldID).getIndex());
            list.add(listItem);
        }
        compound.put(WORLD_LIST_NBT_ID, list);
        compound.putInt(REMAINING_PORTAL_COOLDOWN_NBT_ID, instance.getRemainingPortalCooldown());
        compound.putInt(IN_PORTAL_TIME_NBT_ID, instance.getInPortalTime());
        return compound;
    }

    public static CompoundNBT writeVector3d(Vector3d vec)
    {
        CompoundNBT compound = new CompoundNBT();
        compound.putDouble(X_NBT_ID, vec.x);
        compound.putDouble(Y_NBT_ID, vec.y);
        compound.putDouble(Z_NBT_ID, vec.z);
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
        ListNBT list = compound.getList(WORLD_LIST_NBT_ID, 0);
        for (int i = 0; i < list.size(); i++)
        {
            CompoundNBT listItem = list.getCompound(i);
            String worldID = listItem.getString(WORLD_ID_NBT_ID);
            instance.setLastPortalVec(worldID, readVector3d(listItem.getCompound(LAST_PORTAL_VEC_NBT_ID)));
            instance.setTeleportDirection(worldID, Direction.byIndex(listItem.getInt(TELEPORT_DIRECTION_NBT_ID)));
        }
        instance.setRemainingPortalCooldown(compound.getInt(REMAINING_PORTAL_COOLDOWN_NBT_ID));
        instance.setInPortalTime(compound.getInt(IN_PORTAL_TIME_NBT_ID));
    }

    public static Vector3d readVector3d(CompoundNBT compound)
    {
        double x = compound.getDouble(X_NBT_ID);
        double y = compound.getDouble(Y_NBT_ID);
        double z = compound.getDouble(Z_NBT_ID);
        return new Vector3d(x, y, z);
    }
}
