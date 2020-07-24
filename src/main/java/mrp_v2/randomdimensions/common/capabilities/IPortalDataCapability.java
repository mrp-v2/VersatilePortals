package mrp_v2.randomdimensions.common.capabilities;

import java.util.Set;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public interface IPortalDataCapability {

	public Vector3d getLastPortalVec(String worldID);

	public void setLastPortalVec(String worldID, Vector3d lastPortalVec);

	public Direction getTeleportDirection(String worldID);

	public void setTeleportDirection(String worldID, Direction teleportDirection);

	public boolean getInPortal();

	public void setInPortal(boolean inPortal);

	public void decrementRemainingPortalCooldown();

	public int getRemainingPortalCooldown();

	public void setRemainingPortalCooldown(int remainingPortalCooldown);

	public Set<String> getWorldsWithPortalData();
}
