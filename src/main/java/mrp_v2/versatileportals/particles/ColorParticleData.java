package mrp_v2.versatileportals.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ColorParticleData implements IParticleData
{
    protected final int color;

    public ColorParticleData(int color)
    {
        this.color = color;
    }

    protected static <T extends ColorParticleData> ParticleType<T> createParticleType(Supplier<Codec<T>> codecSupplier,
            IDeserializer<T> deserializerSupplier)
    {
        ParticleType<T> particleType = new ParticleType<T>(false, deserializerSupplier)
        {
            @Override public Codec<T> codec()
            {
                return codecSupplier.get();
            }
        };
        return particleType;
    }

    protected static <T extends ColorParticleData> Codec<T> makeCodec(Function<Integer, T> constructor)
    {
        return RecordCodecBuilder.create(
                (builder) -> builder.group(Codec.INT.fieldOf("color").forGetter(ColorParticleData::getColor))
                        .apply(builder, constructor));
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
            @Override public T fromCommand(ParticleType<T> particleTypeIn, StringReader reader)
                    throws CommandSyntaxException
            {
                reader.expect(' ');
                return constructor.apply(reader.readInt());
            }

            @Override public T fromNetwork(ParticleType<T> particleTypeIn, PacketBuffer buffer)
            {
                return constructor.apply(buffer.readInt());
            }
        };
    }

    @Override public void writeToNetwork(PacketBuffer buffer)
    {
        buffer.writeInt(this.color);
    }

    @Override public String writeToString()
    {
        return String.format(Locale.ROOT, "%s, %d", getID(), this.color);
    }

    abstract String getID();
}
