package mrp_v2.randomdimensions.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import java.util.Locale;
import java.util.function.Function;

public abstract class ColorParticleData implements IParticleData
{
    protected final int color;

    public ColorParticleData(int color)
    {
        this.color = color;
    }

    protected static <T extends ColorParticleData> Codec<T> makeCodec(Function<Integer, T> constructor)
    {
        return RecordCodecBuilder.create(
                (instance1) -> instance1.group(Codec.INT.fieldOf("color").forGetter(ColorParticleData::getColor))
                                        .apply(instance1, constructor));
    }

    public int getColor()
    {
        return this.color;
    }

    protected static <T extends ColorParticleData> IParticleData.IDeserializer<T> makeDeserializer(
            Function<Integer, T> constructor)
    {
        return new IParticleData.IDeserializer<T>()
        {

            @Override public T deserialize(ParticleType<T> particleTypeIn, StringReader reader)
                    throws CommandSyntaxException
            {
                reader.expect(' ');
                int color = reader.readInt();
                return constructor.apply(color);
            }

            @Override public T read(ParticleType<T> particleTypeIn, PacketBuffer buffer)
            {
                return constructor.apply(buffer.readInt());
            }
        };
    }

    @Override public void write(PacketBuffer buffer)
    {
        buffer.writeInt(this.color);
    }

    abstract String getID();

    @Override public String getParameters()
    {
        return String.format(Locale.ROOT, "%s, %d", getID(), this.color);
    }
}
