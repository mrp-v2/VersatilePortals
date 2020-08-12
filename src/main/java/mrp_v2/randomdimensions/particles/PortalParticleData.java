package mrp_v2.randomdimensions.particles;

import com.mojang.serialization.Codec;
import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.particles.ParticleType;

public class PortalParticleData extends ColorParticleData
{
    public static final String ID = "portal";

    public static ParticleType<PortalParticleData> createParticleType()
    {
        ParticleType<PortalParticleData> particleType =
                new ParticleType<PortalParticleData>(false, makeDeserializer(PortalParticleData::new))
                {
                    @Override public Codec<PortalParticleData> func_230522_e_()
                    {
                        return makeCodec(PortalParticleData::new);
                    }
                };
        particleType.setRegistryName(RandomDimensions.ID, ID);
        return particleType;
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
