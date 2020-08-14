package mrp_v2.randomdimensions.common.capabilities;

public class PlayerPortalDataHandler extends PortalDataHandler implements IPlayerPortalDataCapability {

	private int inPortalTime;

	public PlayerPortalDataHandler() {
		super();
		this.inPortalTime = 0;
	}

	@Override
	public int getInPortalTime() {
		return this.inPortalTime;
	}

	@Override
	public int incrementInPortalTime() {
		return ++this.inPortalTime;
	}

	@Override
	public void setInPortalTime(int inPortalTime) {
		if (this.inPortalTime != inPortalTime) {
			this.inPortalTime = inPortalTime;
		}
	}
}
