package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.TextureProvider;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import mrp_v2.mrplibrary.datagen.providers.TextureProvider.FinishedTextureConsumer;
import mrp_v2.mrplibrary.datagen.providers.TextureProvider.Texture;

public class TextureGenerator extends TextureProvider
{
    public TextureGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper, String modId)
    {
        super(generator, existingFileHelper, modId);
    }

    @Override protected void addTextures(FinishedTextureConsumer finishedTextureConsumer)
    {
        ResourceLocation netherPortalTextureLoc =
                new ResourceLocation("block/" + Blocks.NETHER_PORTAL.getRegistryName().getPath());
        Texture portalTexture = getTexture(netherPortalTextureLoc);
        makeGrayscale(portalTexture.getTexture());
        adjustLevels(portalTexture.getTexture(), 0.35d);
        finish(portalTexture, new ResourceLocation(VersatilePortals.ID, "block/" + PortalBlock.ID),
                finishedTextureConsumer);
        ResourceLocation lapisBlockTextureLoc =
                new ModelResourceLocation("block/" + Blocks.LAPIS_BLOCK.getRegistryName().getPath());
        Texture portalFrameTexture = getTexture(lapisBlockTextureLoc);
        makeGrayscale(portalFrameTexture.getTexture());
        adjustLevels(portalFrameTexture.getTexture(), 0.35d);
        finish(portalFrameTexture, new ResourceLocation(VersatilePortals.ID, "block/" + PortalFrameBlock.ID),
                finishedTextureConsumer);
        portalFrameTexture.getTexture()
                .setRGB(3, 2, 10, 1, portalFrameTexture.getTexture().getRGB(3, 15, 10, 1, null, 0, 10), 0, 10);
        portalFrameTexture.getTexture()
                .setRGB(3, 13, 10, 1, portalFrameTexture.getTexture().getRGB(3, 0, 10, 1, null, 0, 10), 0, 10);
        portalFrameTexture.getTexture()
                .setRGB(2, 2, 1, 12, portalFrameTexture.getTexture().getRGB(15, 2, 1, 12, null, 0, 1), 0, 1);
        portalFrameTexture.getTexture()
                .setRGB(13, 2, 1, 12, portalFrameTexture.getTexture().getRGB(0, 2, 1, 12, null, 0, 1), 0, 1);
        finish(portalFrameTexture, new ResourceLocation(VersatilePortals.ID, "block/" + PortalControllerBlock.ID),
                finishedTextureConsumer);
        ResourceLocation rubyTextureLoc = new ModelResourceLocation("item/ruby");
        Texture controlTexture = getTexture(rubyTextureLoc);
        makeGrayscale(controlTexture.getTexture());
        adjustLevels(controlTexture.getTexture(), 0.35d);
        finish(controlTexture, new ResourceLocation(VersatilePortals.ID, "item/" + ExistingWorldControlItem.ID),
                finishedTextureConsumer);
    }
}
