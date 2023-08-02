package mrp_v2.versatileportals.particles;

import com.mojang.serialization.Codec;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.core.particles.ParticleType;

import net.minecraft.core.particles.ParticleOptions.Deserializer;

public class PortalControllerParticleData extends ColorParticleData
{
    public static final Codec<PortalControllerParticleData> CODEC = makeCodec(PortalControllerParticleData::new);
    public static final Deserializer<PortalControllerParticleData> DESERIALIZER =
            makeDeserializer(PortalControllerParticleData::new);
    public static final String ID = PortalControllerBlock.ID;

    public PortalControllerParticleData(int color)
    {
        super(color);
    }

    public static ParticleType<PortalControllerParticleData> createParticleType()
    {
        return ColorParticleData.createParticleType(() -> CODEC, DESERIALIZER);
    }

    @Override String getID()
    {
        return ID;
    }

    @Override public ParticleType<?> getType()
    {
        return ObjectHolder.PORTAL_CONTROLLER_PARTICLE_TYPE.get();
    }
}
