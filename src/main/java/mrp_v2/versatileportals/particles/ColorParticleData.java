package mrp_v2.versatileportals.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.particles.ParticleOptions.Deserializer;

public abstract class ColorParticleData implements ParticleOptions
{
    protected final int color;

    public ColorParticleData(int color)
    {
        this.color = color;
    }

    protected static <T extends ColorParticleData> ParticleType<T> createParticleType(Supplier<Codec<T>> codecSupplier,
                                                                                      Deserializer<T> deserializerSupplier)
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

    protected static <T extends ColorParticleData> ParticleOptions.Deserializer<T> makeDeserializer(
            Function<Integer, T> constructor)
    {
        return new ParticleOptions.Deserializer<T>()
        {
            @Override public T fromCommand(ParticleType<T> particleTypeIn, StringReader reader)
                    throws CommandSyntaxException
            {
                reader.expect(' ');
                return constructor.apply(reader.readInt());
            }

            @Override
            public T fromNetwork(ParticleType<T> particleTypeIn, FriendlyByteBuf buffer)
            {
                return constructor.apply(buffer.readInt());
            }
        };
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeInt(this.color);
    }

    @Override public String writeToString()
    {
        return String.format(Locale.ROOT, "%s, %d", getID(), this.color);
    }

    abstract String getID();
}
