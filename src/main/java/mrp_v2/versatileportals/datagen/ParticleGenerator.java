package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.ParticleDescriptionProvider;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator extends ParticleDescriptionProvider
{
    protected ParticleGenerator(PackOutput output, String modId, ExistingFileHelper existingFileHelper)
    {
        super(output, modId, existingFileHelper);
    }

    @Override
    protected void addDescriptions() {
        List<ResourceLocation> particleTextures = new ArrayList<>();
        particleTextures.add(new ResourceLocation("generic_0"));
        particleTextures.add(new ResourceLocation("generic_1"));
        particleTextures.add(new ResourceLocation("generic_2"));
        particleTextures.add(new ResourceLocation("generic_3"));
        particleTextures.add(new ResourceLocation("generic_4"));
        particleTextures.add(new ResourceLocation("generic_5"));
        particleTextures.add(new ResourceLocation("generic_6"));
        particleTextures.add(new ResourceLocation("generic_7"));
        spriteSet(ObjectHolder.PORTAL_PARTICLE_TYPE.get(), particleTextures);
        spriteSet(ObjectHolder.PORTAL_CONTROLLER_PARTICLE_TYPE.get(), particleTextures);
    }
}
