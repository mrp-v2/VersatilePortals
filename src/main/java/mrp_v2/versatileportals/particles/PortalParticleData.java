package mrp_v2.versatileportals.particles;

import com.mojang.serialization.Codec;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.particles.ParticleType;

public class PortalParticleData extends ColorParticleData
{
    public static final Codec<PortalParticleData> CODEC = makeCodec(PortalParticleData::new);
    public static final IDeserializer<PortalParticleData> DESERIALIZER = makeDeserializer(PortalParticleData::new);
    public static final String ID = "portal";

    public PortalParticleData(int color)
    {
        super(color);
    }

    public static ParticleType<PortalParticleData> createParticleType()
    {
        return ColorParticleData.createParticleType(() -> CODEC, DESERIALIZER, ID);
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
