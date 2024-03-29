package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.TextureProvider;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class TextureGenerator extends TextureProvider {
    public TextureGenerator(PackOutput output, ExistingFileHelper existingFileHelper, String modId) {
        super(output, existingFileHelper, modId);
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    @Override
    protected void addTextures(FinishedTextureConsumer finishedTextureConsumer) {
        ResourceLocation netherPortalTextureLoc =
                new ResourceLocation("block/" + key(Blocks.NETHER_PORTAL).getPath());
        Texture portalTexture = getTexture(netherPortalTextureLoc);
        makeGrayscale(portalTexture.getTexture());
        adjustLevels(portalTexture.getTexture(), 0.35d);
        finish(portalTexture, new ResourceLocation(VersatilePortals.ID, "block/" + PortalBlock.ID),
                finishedTextureConsumer);
        ResourceLocation lapisBlockTextureLoc =
                ModelResourceLocation.vanilla("block/" + key(Blocks.LAPIS_BLOCK).getPath(), BlockModelShaper.statePropertiesToString(Blocks.LAPIS_BLOCK.defaultBlockState().getValues()));
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
    }
}
