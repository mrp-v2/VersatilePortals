package mrp_v2.randomdimensions.common.capabilities;

import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerPortalDataProvider extends PortalDataProvider {

	private final PlayerPortalDataHandler playerPortalDataHandler;
	private final LazyOptional<PlayerPortalDataHandler> portalDataHandlerLazyOptional;

	public PlayerPortalDataProvider() {
		this.playerPortalDataHandler = new PlayerPortalDataHandler();
		this.portalDataHandlerLazyOptional = LazyOptional.of(() -> this.playerPortalDataHandler);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityHandler.PLAYER_PORTAL_DATA_CAPABILITY) {
			return this.portalDataHandlerLazyOptional.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT serializeNBT() {
		return (CompoundNBT) ObjectHolder.PLAYER_PORTAL_DATA_STORAGE
				.writeNBT(CapabilityHandler.PLAYER_PORTAL_DATA_CAPABILITY, this.playerPortalDataHandler, null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ObjectHolder.PLAYER_PORTAL_DATA_STORAGE.readNBT(CapabilityHandler.PLAYER_PORTAL_DATA_CAPABILITY,
				this.playerPortalDataHandler, null, nbt);
	}
}
