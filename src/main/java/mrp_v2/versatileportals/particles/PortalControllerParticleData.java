package mrp_v2.versatileportals.particles;

import com.mojang.serialization.Codec;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.particles.ParticleType;

public class PortalControllerParticleData extends ColorParticleData
{
    public static final Codec<PortalControllerParticleData> CODEC = makeCodec(PortalControllerParticleData::new);
    public static final IDeserializer<PortalControllerParticleData> DESERIALIZER =
            makeDeserializer(PortalControllerParticleData::new);
    public static final String ID = "portal_controller";

    public PortalControllerParticleData(int color)
    {
        super(color);
    }

    public static ParticleType<PortalControllerParticleData> createParticleType()
    {
        return ColorParticleData.createParticleType(() -> CODEC, DESERIALIZER, ID);
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
