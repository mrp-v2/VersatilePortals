package mrp_v2.randomdimensions.common.capabilities;

public interface IPlayerPortalDataCapability extends IPortalDataCapability {

    int getInPortalTime();

    void setInPortalTime(int inPortalTime);

    int incrementInPortalTime();
}
