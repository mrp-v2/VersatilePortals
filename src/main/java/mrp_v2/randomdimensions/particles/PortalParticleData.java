package mrp_v2.randomdimensions.particles;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class PortalParticleData implements IParticleData {

	public static final String ID = "portal";

	public static final Codec<PortalParticleData> CODEC = RecordCodecBuilder.create((instance1) -> {
		return instance1.group(Codec.INT.fieldOf("color").forGetter((instance2) -> {
			return instance2.getColor();
		})).apply(instance1, PortalParticleData::new);
	});

	private static final boolean ALWAYS_SHOW = false;

	@SuppressWarnings("deprecation")
	public static final IParticleData.IDeserializer<PortalParticleData> DESERIALIZER = new IParticleData.IDeserializer<PortalParticleData>() {

		@Override
		public PortalParticleData deserialize(ParticleType<PortalParticleData> particleTypeIn, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			int color = reader.readInt();
			return new PortalParticleData(color);
		}

		@Override
		public PortalParticleData read(ParticleType<PortalParticleData> particleTypeIn, PacketBuffer buffer) {
			return new PortalParticleData(buffer.readInt());
		}
	};

	private final int color;

	public PortalParticleData(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeInt(this.color);
	}

	public static ParticleType<PortalParticleData> createParticleType() {
		ParticleType<PortalParticleData> particleType = new ParticleType<PortalParticleData>(ALWAYS_SHOW,
				DESERIALIZER) {

			@Override
			public Codec<PortalParticleData> func_230522_e_() {
				return PortalParticleData.CODEC;
			}
		};
		particleType.setRegistryName(RandomDimensions.ID, ID);
		return particleType;
	}

	@Override
	public ParticleType<?> getType() {
		return ObjectHolder.PORTAL_PARTICLE;
	}

	@Override
	public String getParameters() {
		return String.format(Locale.ROOT, "%s, %d", ID, this.color);
	}
}
