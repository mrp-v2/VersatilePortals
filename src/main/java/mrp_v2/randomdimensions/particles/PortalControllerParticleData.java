package mrp_v2.randomdimensions.particles;

import com.mojang.serialization.Codec;
import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.particles.ParticleType;

public class PortalControllerParticleData extends ColorParticleData
{
    public static final String ID = "portal_controller";

    public static ParticleType<PortalControllerParticleData> createParticleType()
    {
        ParticleType<PortalControllerParticleData> particleType = new ParticleType<PortalControllerParticleData>(false,
                makeDeserializer(PortalControllerParticleData::new))
        {

            @Override public Codec<PortalControllerParticleData> func_230522_e_()
            {
                return makeCodec(PortalControllerParticleData::new);
            }
        };
        particleType.setRegistryName(RandomDimensions.ID, ID);
        return particleType;
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
