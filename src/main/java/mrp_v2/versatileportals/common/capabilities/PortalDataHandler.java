package mrp_v2.versatileportals.common.capabilities;

public class PortalDataHandler implements IPortalDataCapability
{
    private int remainingPortalCooldown;
    private int inPortalTime;

    public PortalDataHandler()
    {
        this.remainingPortalCooldown = 0;
        this.inPortalTime = 0;
    }

    @Override public void decrementRemainingPortalCooldown()
    {
        if (this.remainingPortalCooldown > 0)
        {
            this.remainingPortalCooldown--;
        }
    }

    @Override public int getRemainingPortalCooldown()
    {
        return this.remainingPortalCooldown;
    }

    @Override public void setRemainingPortalCooldown(int remainingPortalCooldown)
    {
        this.remainingPortalCooldown = remainingPortalCooldown;
    }

    @Override public int getInPortalTime()
    {
        return this.inPortalTime;
    }

    @Override public void setInPortalTime(int inPortalTime)
    {
        this.inPortalTime = inPortalTime;
    }

    @Override public int incrementInPortalTime()
    {
        return ++this.inPortalTime;
    }
}
