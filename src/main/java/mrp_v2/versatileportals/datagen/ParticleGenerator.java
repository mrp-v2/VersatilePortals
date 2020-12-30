package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.ParticleProvider;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class ParticleGenerator extends ParticleProvider
{
    protected ParticleGenerator(DataGenerator generator, String modId)
    {
        super(generator, modId);
    }

    @Override protected void registerParticles(Consumer<ParticleBuilder> consumer)
    {
        ResourceLocation[] particleTextures =
                new ResourceLocation[]{new ResourceLocation("generic_0"), new ResourceLocation("generic_1"),
                        new ResourceLocation("generic_2"), new ResourceLocation("generic_3"),
                        new ResourceLocation("generic_4"), new ResourceLocation("generic_5"),
                        new ResourceLocation("generic_6"), new ResourceLocation("generic_7")};
        consumer.accept(
                ParticleProvider.makeBuilder(ObjectHolder.PORTAL_PARTICLE_TYPE.getId()).addTextures(particleTextures));
        consumer.accept(ParticleProvider.makeBuilder(ObjectHolder.PORTAL_CONTROLLER_PARTICLE_TYPE.getId())
                .addTextures(particleTextures));
    }
}
