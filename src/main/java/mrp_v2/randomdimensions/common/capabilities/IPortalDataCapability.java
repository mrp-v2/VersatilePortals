package mrp_v2.randomdimensions.common.capabilities;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Set;

public interface IPortalDataCapability {

    Vector3d getLastPortalVec(String worldID);

    void setLastPortalVec(String worldID, Vector3d lastPortalVec);

    Direction getTeleportDirection(String worldID);

    void setTeleportDirection(String worldID, Direction teleportDirection);

    void decrementRemainingPortalCooldown();

    int getRemainingPortalCooldown();

    void setRemainingPortalCooldown(int remainingPortalCooldown);

    int getTimeInPortal();

    void setTimeInPortal(int timeInPortal);

    Set<String> getWorldsWithPortalData();
}
