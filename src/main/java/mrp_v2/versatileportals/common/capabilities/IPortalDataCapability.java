package mrp_v2.versatileportals.common.capabilities;

public interface IPortalDataCapability {

    void decrementRemainingPortalCooldown();

    int getRemainingPortalCooldown();

    void setRemainingPortalCooldown(int remainingPortalCooldown);

    int getInPortalTime();

    void setInPortalTime(int inPortalTime);

    int incrementInPortalTime();
}
