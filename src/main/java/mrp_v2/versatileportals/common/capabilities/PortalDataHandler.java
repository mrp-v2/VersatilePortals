package mrp_v2.versatileportals.common.capabilities;

import net.minecraft.util.math.BlockPos;

public class PortalDataHandler implements IPortalDataCapability
{
    private int remainingPortalCooldown;
    private int inPortalTime;
    private boolean isInPortal;
    private BlockPos portalPos;

    public PortalDataHandler()
    {
        this(0, 0, false, BlockPos.ZERO);
    }

    public PortalDataHandler(int remainingPortalCooldown, int inPortalTime, boolean isInPortal, BlockPos portalPos)
    {
        this.remainingPortalCooldown = remainingPortalCooldown;
        this.inPortalTime = inPortalTime;
        this.isInPortal = isInPortal;
        this.portalPos = portalPos;
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

    @Override public boolean getInPortal()
    {
        return this.isInPortal;
    }

    @Override public void setInPortal(boolean isInPortal)
    {
        this.isInPortal = isInPortal;
    }

    @Override public void setPortal(BlockPos portalPos)
    {
        setPortalPos(portalPos);
        setInPortal(true);
    }

    @Override public BlockPos getPortalPos()
    {
        return this.portalPos;
    }

    @Override public void setPortalPos(BlockPos portalPos)
    {
        this.portalPos = portalPos.immutable();
    }
}
