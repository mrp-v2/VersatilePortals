package mrp_v2.randomdimensions.common.capabilities;

public interface IPlayerPortalDataCapability extends IPortalDataCapability {

	public int getInPortalTime();

	public int incrementInPortalTime();

	public void setInPortalTime(int inPortalTime);
}
