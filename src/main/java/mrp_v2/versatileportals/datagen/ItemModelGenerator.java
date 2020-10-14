package mrp_v2.versatileportals.datagen;

import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import mrp_v2.versatileportals.item.EmptyExistingWorldControlItem;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.item.PortalLighter;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
    {
        super(generator, modid, existingFileHelper);
    }

    @Override protected void registerModels()
    {
        singleTexture(EmptyExistingWorldControlItem.ID, mcLoc("item/generated"), "layer0",
                modLoc("item/" + ExistingWorldControlItem.ID));
        singleTexture(ExistingWorldControlItem.ID, mcLoc("item/generated"), "layer0",
                modLoc("item/" + ExistingWorldControlItem.ID));
        withExistingParent(PortalFrameBlock.ID, modLoc("block/" + PortalFrameBlock.ID));
        withExistingParent(PortalControllerBlock.ID, modLoc("block/" + PortalControllerBlock.ID));
        singleTexture(PortalLighter.ID, mcLoc("item/generated"), "layer0", modLoc("item/" + PortalLighter.ID));
    }
}
