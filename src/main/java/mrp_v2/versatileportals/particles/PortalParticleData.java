package mrp_v2.versatileportals.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class PortalParticleData extends ColorParticleData
{
    public static final Codec<PortalParticleData> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(Codec.INT.fieldOf("color").forGetter(ColorParticleData::getColor),
                    Codec.BOOL.fieldOf("isOnYAxis").forGetter(PortalParticleData::isOnYAxis))
            .apply(builder, PortalParticleData::new));
    public static final IDeserializer<PortalParticleData> DESERIALIZER =
            new IParticleData.IDeserializer<PortalParticleData>()
            {
                @Override public PortalParticleData fromCommand(ParticleType<PortalParticleData> particleTypeIn,
                        StringReader reader) throws CommandSyntaxException
                {
                    reader.expect(' ');
                    return new PortalParticleData(reader.readInt(), reader.readBoolean());
                }

                @Override public PortalParticleData fromNetwork(ParticleType<PortalParticleData> particleTypeIn,
                        PacketBuffer buffer)
                {
                    return new PortalParticleData(buffer.readInt(), buffer.readBoolean());
                }
            };
    public static final String ID = PortalBlock.ID;
    private final boolean isOnYAxis;

    public PortalParticleData(int color, boolean isOnYAxis)
    {
        super(color);
        this.isOnYAxis = isOnYAxis;
    }

    public static ParticleType<PortalParticleData> createParticleType()
    {
        return ColorParticleData.createParticleType(() -> CODEC, DESERIALIZER);
    }

    @Override String getID()
    {
        return ID;
    }

    @Override public ParticleType<?> getType()
    {
        return ObjectHolder.PORTAL_PARTICLE_TYPE.get();
    }

    public boolean isOnYAxis()
    {
        return this.isOnYAxis;
    }
}
