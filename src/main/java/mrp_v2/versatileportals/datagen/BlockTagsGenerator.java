package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.BlockTagsProvider;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class BlockTagsGenerator extends BlockTagsProvider {
    public BlockTagsGenerator(DataGenerator generatorIn, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ObjectHolder.PORTAL_FRAME_BLOCK.get())
                .add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ObjectHolder.PORTAL_FRAME_BLOCK.get()).add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
    }
}
