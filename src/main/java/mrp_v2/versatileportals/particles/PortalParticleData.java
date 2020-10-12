package mrp_v2.versatileportals.particles;

import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.particles.ParticleType;

public class PortalParticleData extends ColorParticleData
{
    public static final String ID = "portal";

    public static ParticleType<PortalParticleData> createParticleType()
    {
        return ColorParticleData.createParticleType(PortalParticleData::new, ID);
    }

    public PortalParticleData(int color)
    {
        super(color);
    }

    @Override String getID()
    {
        return ID;
    }

    @Override public ParticleType<?> getType()
    {
        return ObjectHolder.PORTAL_PARTICLE_TYPE;
    }
}
