package mrp_v2.versatileportals.datagen;

import mrp_v2.mrp_v2datagenlibrary.datagen.DataGeneratorHelper;
import mrp_v2.versatileportals.VersatilePortals;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = VersatilePortals.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenHandler
{
    @SubscribeEvent public static void gatherDataEvent(final GatherDataEvent event)
    {
        DataGeneratorHelper helper = new DataGeneratorHelper(event, VersatilePortals.ID);
        if (event.includeServer())
        {
            helper.addLootTables(new LootTables());
            helper.addRecipeGenerator(RecipeGenerator::new);
        }
        if (event.includeClient())
        {
            helper.addBlockStateProvider(BlockStateGenerator::new);
            helper.addItemModelProvider(ItemModelGenerator::new);
            helper.addLanguageProvider(EN_USTranslationGenerator::new);
        }
    }
}
