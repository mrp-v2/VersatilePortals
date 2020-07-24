package mrp_v2.randomdimensions.common.capabilities;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;

import com.google.common.collect.Maps;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class PortalDataHandler implements IPortalDataCapability {

	private final Map<String, MutablePair<Vector3d, Direction>> data;
	private boolean inPortal;
	private int remainingPortalCooldown;

	public PortalDataHandler() {
		this.data = Maps.newHashMap();
		this.inPortal = false;
		this.remainingPortalCooldown = 0;
	}

	@Override
	public Vector3d getLastPortalVec(String worldID) {
		return this.data.get(worldID).getLeft();
	}

	@Override
	public void setLastPortalVec(String worldID, Vector3d lastPortalVec) {
		this.ensureExists(worldID);
		this.data.get(worldID).setLeft(lastPortalVec);
	}

	@Override
	public Direction getTeleportDirection(String worldID) {
		return this.data.get(worldID).getRight();
	}

	@Override
	public void setTeleportDirection(String worldID, Direction teleportDirection) {
		this.ensureExists(worldID);
		this.data.get(worldID).setRight(teleportDirection);
	}

	private void ensureExists(String worldID) {
		this.data.putIfAbsent(worldID, new MutablePair<Vector3d, Direction>());
	}

	@Override
	public Set<String> getWorldsWithPortalData() {
		return this.data.keySet();
	}

	@Override
	public boolean getInPortal() {
		return this.inPortal;
	}

	@Override
	public void setInPortal(boolean inPortal) {
		if (this.inPortal != inPortal) {
			this.inPortal = inPortal;
		}
	}

	@Override
	public void decrementRemainingPortalCooldown() {
		if (this.remainingPortalCooldown > 0) {
			this.remainingPortalCooldown--;
		}
	}

	@Override
	public int getRemainingPortalCooldown() {
		return this.remainingPortalCooldown;
	}

	@Override
	public void setRemainingPortalCooldown(int remainingPortalCooldown) {
		this.remainingPortalCooldown = remainingPortalCooldown;
	}
}
