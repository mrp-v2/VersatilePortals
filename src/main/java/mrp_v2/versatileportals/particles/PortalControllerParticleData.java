package mrp_v2.versatileportals.particles;

import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.particles.ParticleType;

public class PortalControllerParticleData extends ColorParticleData
{
    public static final String ID = "portal_controller";

    public static ParticleType<PortalControllerParticleData> createParticleType()
    {
        return ColorParticleData.createParticleType(PortalControllerParticleData::new, ID);
    }

    public PortalControllerParticleData(int color)
    {
        super(color);
    }

    @Override String getID()
    {
        return ID;
    }

    @Override public ParticleType<?> getType()
    {
        return ObjectHolder.PORTAL_CONTROLLER_PARTICLE_TYPE;
    }
}
