package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.DataGeneratorHelper;
import mrp_v2.versatileportals.VersatilePortals;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = VersatilePortals.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenHandler
{
    @SubscribeEvent public static void gatherDataEvent(final GatherDataEvent event)
    {
        DataGeneratorHelper helper = new DataGeneratorHelper(event, VersatilePortals.ID);
        helper.addLootTables(new LootTables());
        helper.addRecipeProvider(RecipeGenerator::new);
        helper.addTextureProvider(TextureGenerator::new);
        helper.addParticleProvider(ParticleGenerator::new);
        helper.addBlockStateProvider(BlockStateGenerator::new);
        helper.addItemModelProvider(ItemModelGenerator::new);
        helper.addLanguageProvider(EN_USTranslationGenerator::new);
    }
}
